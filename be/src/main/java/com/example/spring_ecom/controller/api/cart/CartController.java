package com.example.spring_ecom.controller.api.cart;

import com.example.spring_ecom.controller.api.cart.model.AddToCartRequest;
import com.example.spring_ecom.controller.api.cart.model.AddToCartRequestMapper;
import com.example.spring_ecom.controller.api.cart.model.CartItemResponse;
import com.example.spring_ecom.controller.api.cart.model.CartItemResponseMapper;
import com.example.spring_ecom.controller.api.cart.model.UpdateCartItemRequest;
import com.example.spring_ecom.controller.api.cart.model.UpdateCartItemRequestMapper;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.util.SecurityUtil;
import com.example.spring_ecom.domain.cart.CartItem;
import com.example.spring_ecom.service.cart.CartUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CartController implements CartAPI {
    
    private final CartUseCase cartUseCase;
    private final CartItemResponseMapper responseMapper;
    private final AddToCartRequestMapper addToCartRequestMapper;
    private final UpdateCartItemRequestMapper updateCartItemRequestMapper;
    
    @Override
    public ApiResponse<List<CartItemResponse>> getCartItems() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<CartItemResponse> items = cartUseCase.getCartItems(userId).stream()
                .map(responseMapper::toResDto)
                .toList();
        return ApiResponse.Success.of(items);
    }
    
    @Override
    public ApiResponse<CartItemResponse> addItemToCart(AddToCartRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        
        CartItem cartItemRequest = addToCartRequestMapper.toDomain(request);
        
        CartItemResponse item = responseMapper.toResDto(
                cartUseCase.addItemToCart(userId, cartItemRequest));
        return ApiResponse.Success.of(item);
    }
    
    @Override
    public ApiResponse<CartItemResponse> updateCartItemQuantity(Long productId, UpdateCartItemRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        
        CartItem updateRequest = updateCartItemRequestMapper.toDomain(request);
        
        CartItemResponse item = responseMapper.toResDto(
                cartUseCase.updateCartItemQuantity(userId, productId, updateRequest));
        return ApiResponse.Success.of(item);
    }
    
    @Override
    public ApiResponse<Void> removeItemFromCart(Long productId) {
        Long userId = SecurityUtil.getCurrentUserId();
        cartUseCase.removeItemFromCart(userId, productId);
        return ApiResponse.Success.of();
    }
    
    @Override
    public ApiResponse<Void> clearCart() {
        Long userId = SecurityUtil.getCurrentUserId();
        cartUseCase.clearCart(userId);
        return ApiResponse.Success.of();
    }

    @Override
    public ApiResponse<Void> syncCart(List<AddToCartRequest> items) {
        Long userId = SecurityUtil.getCurrentUserId();
        for (AddToCartRequest item : items) {
            CartItem cartItemRequest = addToCartRequestMapper.toDomain(item);
            cartUseCase.addItemToCart(userId, cartItemRequest);
        }
        return ApiResponse.Success.of();
    }
}


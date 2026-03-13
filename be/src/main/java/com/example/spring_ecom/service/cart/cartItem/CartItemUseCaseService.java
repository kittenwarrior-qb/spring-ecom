package com.example.spring_ecom.service.cart.cartItem;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.cart.Cart;
import com.example.spring_ecom.domain.cart.CartItem;
import com.example.spring_ecom.repository.database.cart.CartEntity;
import com.example.spring_ecom.service.cart.CartCommandService;
import com.example.spring_ecom.service.cart.CartQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemUseCaseService implements CartItemUseCase {
    
    private final CartItemQueryService cartItemQueryService;
    private final CartItemCommandService cartItemCommandService;
    private final CartQueryService cartQueryService;
    private final CartCommandService cartCommandService;
    
    @Override
    @Transactional(readOnly = true)
    public List<CartItem> getCartItems(Long cartId) {
        return cartItemQueryService.findByCartId(cartId);
    }
    
    @Override
    @Transactional
    public CartItem addItemToCart(Long userId, CartItem cartItemRequest) {
        CartEntity cart = getOrCreateCart(userId);
        return cartItemCommandService.addItemToCart(cart, cartItemRequest);
    }
    
    @Override
    @Transactional
    public CartItem updateCartItemQuantity(Long userId, Long productId, CartItem updateRequest) {
        CartEntity cart = cartQueryService.findEntityByUserId(userId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Cart not found"));
        
        return cartItemCommandService.updateCartItemQuantity(cart.getId(), productId, updateRequest);
    }
    
    @Override
    @Transactional
    public void removeItemFromCart(Long userId, Long productId) {
        CartEntity cart = cartQueryService.findEntityByUserId(userId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Cart not found"));
        
        cartItemCommandService.removeItemFromCart(cart.getId(), productId);
    }
    
    @Override
    @Transactional
    public void clearCartItems(Long userId) {
        CartEntity cart = cartQueryService.findEntityByUserId(userId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Cart not found"));
        
        cartItemCommandService.clearCartItems(cart.getId());
    }
    
    private CartEntity getOrCreateCart(Long userId) {
        return cartQueryService.findEntityByUserId(userId)
                .orElseGet(() -> {
                    // Create new cart through CartCommandService
                    Cart newCart = new Cart(null, userId, null, null);
                    Cart createdCart = cartCommandService.createCart(newCart);
                    return cartQueryService.findEntityByUserId(userId)
                            .orElseThrow(() -> new BaseException(ResponseCode.INTERNAL_SERVER_ERROR, "Failed to create cart"));
                });
    }
}
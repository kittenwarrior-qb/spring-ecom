package com.example.spring_ecom.service.cart;

import com.example.spring_ecom.domain.cart.Cart;
import com.example.spring_ecom.domain.cart.CartItem;
import com.example.spring_ecom.service.cart.cartItem.CartItemUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartUseCase {
    
    private final CartCommandService commandService;
    private final CartQueryService queryService;
    private final CartItemUseCase cartItemUseCase;
    
    // ========== COMMAND OPERATIONS ==========
    
    public Cart createCart(Cart cart) {
        return commandService.createCart(cart);
    }
    
    public void deleteCart(Long userId) {
        commandService.deleteCart(userId);
    }
    
    public void clearCart(Long userId) {
        commandService.clearCart(userId);
    }
    
    public CartItem addItemToCart(Long userId, CartItem cartItemRequest) {
        return cartItemUseCase.addItemToCart(userId, cartItemRequest);
    }
    
    public CartItem updateCartItemQuantity(Long userId, Long productId, CartItem updateRequest) {
        return cartItemUseCase.updateCartItemQuantity(userId, productId, updateRequest);
    }
    
    public void removeItemFromCart(Long userId, Long productId) {
        cartItemUseCase.removeItemFromCart(userId, productId);
    }
    
    // ========== QUERY OPERATIONS ==========
    
    public Optional<Cart> getCartByUserId(Long userId) {
        return queryService.findByUserId(userId);
    }
    
    public List<CartItem> getCartItems(Long userId) {
        return queryService.findCartItems(userId);
    }
}

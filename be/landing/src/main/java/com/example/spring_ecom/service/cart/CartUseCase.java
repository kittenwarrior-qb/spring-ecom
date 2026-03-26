package com.example.spring_ecom.service.cart;

import com.example.spring_ecom.domain.cart.Cart;
import com.example.spring_ecom.domain.cart.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartUseCase {
    
    // ========== COMMAND OPERATIONS ==========
    
    Cart createCart(Cart cart);
    
    void deleteCart(Long userId);
    
    void clearCart(Long userId);
    
    CartItem addItemToCart(Long userId, CartItem cartItemRequest);
    
    CartItem updateCartItemQuantity(Long userId, Long productId, CartItem updateRequest);
    
    void removeItemFromCart(Long userId, Long productId);
    
    // ========== QUERY OPERATIONS ==========
    
    Optional<Cart> getCartByUserId(Long userId);
    
    List<CartItem> getCartItems(Long userId);
}

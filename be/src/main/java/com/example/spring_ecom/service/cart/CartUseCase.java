package com.example.spring_ecom.service.cart;

import com.example.spring_ecom.domain.cart.Cart;
import com.example.spring_ecom.domain.cart.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartUseCase {
    
    Optional<Cart> getCartByUserId(Long userId);
    
    List<CartItem> getCartItems(Long userId);
    
    CartItem addItemToCart(Long userId, Long productId, Integer quantity);
    
    CartItem updateCartItemQuantity(Long userId, Long productId, Integer quantity);
    
    void removeItemFromCart(Long userId, Long productId);
    
    void clearCart(Long userId);
}

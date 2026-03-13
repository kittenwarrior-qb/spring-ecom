package com.example.spring_ecom.service.cart.cartItem;

import com.example.spring_ecom.domain.cart.CartItem;

import java.util.List;

public interface CartItemUseCase {
    
    List<CartItem> getCartItems(Long cartId);
    
    CartItem addItemToCart(Long userId, CartItem cartItemRequest);
    
    CartItem updateCartItemQuantity(Long userId, Long productId, CartItem updateRequest);
    
    void removeItemFromCart(Long userId, Long productId);
    
    void clearCartItems(Long userId);
}
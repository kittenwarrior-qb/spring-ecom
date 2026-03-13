package com.example.spring_ecom.service.cart;

import com.example.spring_ecom.domain.cart.Cart;
import com.example.spring_ecom.domain.cart.CartItem;
import com.example.spring_ecom.service.cart.cartItem.CartItemUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartUseCaseService implements CartUseCase {
    
    private final CartQueryService queryService;
    private final CartCommandService commandService;
    private final CartItemUseCase cartItemUseCase;
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Cart> getCartByUserId(Long userId) {
        return queryService.findByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CartItem> getCartItems(Long userId) {
        return queryService.findByUserId(userId)
                .map(cart -> queryService.findCartItems(cart.id()))
                .orElse(List.of());
    }
    
    @Override
    @Transactional
    public CartItem addItemToCart(Long userId, CartItem cartItemRequest) {
        // Ensure cart exists first, then delegate to CartItemUseCase
        getOrCreateCart(userId);
        return cartItemUseCase.addItemToCart(userId, cartItemRequest);
    }
    
    @Override
    @Transactional
    public CartItem updateCartItemQuantity(Long userId, Long productId, CartItem updateRequest) {
        return cartItemUseCase.updateCartItemQuantity(userId, productId, updateRequest);
    }
    
    @Override
    @Transactional
    public void removeItemFromCart(Long userId, Long productId) {
        cartItemUseCase.removeItemFromCart(userId, productId);
    }
    
    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartItemUseCase.clearCartItems(userId);
    }
    
    private Cart getOrCreateCart(Long userId) {
        return queryService.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart(null, userId, null, null);
                    return commandService.createCart(newCart);
                });
    }
}

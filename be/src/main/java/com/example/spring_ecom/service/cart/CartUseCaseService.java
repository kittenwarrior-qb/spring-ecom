package com.example.spring_ecom.service.cart;

import com.example.spring_ecom.domain.cart.Cart;
import com.example.spring_ecom.domain.cart.CartItem;
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
    
    @Override
    @Transactional
    public Optional<Cart> getCartByUserId(Long userId) {
        return queryService.findByUserId(userId);
    }
    
    @Override
    @Transactional
    public List<CartItem> getCartItems(Long userId) {
        return queryService.findByUserId(userId)
                .map(cart -> queryService.findCartItems(cart.id()))
                .orElse(List.of());
    }
    
    @Override
    @Transactional
    public CartItem addItemToCart(Long userId, Long productId, Integer quantity) {
        return commandService.addItemToCart(userId, productId, quantity);
    }
    
    @Override
    @Transactional
    public CartItem updateCartItemQuantity(Long userId, Long productId, Integer quantity) {
        return commandService.updateCartItemQuantity(userId, productId, quantity);
    }
    
    @Override
    @Transactional
    public void removeItemFromCart(Long userId, Long productId) {
        commandService.removeItemFromCart(userId, productId);
    }
    
    @Override
    @Transactional
    public void clearCart(Long userId) {
        commandService.clearCart(userId);
    }
}

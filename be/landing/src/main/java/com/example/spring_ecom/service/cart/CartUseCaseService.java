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
    
    private final CartCommandService commandService;
    private final CartQueryService queryService;
    private final CartItemUseCase cartItemUseCase;
    
    // ========== COMMAND OPERATIONS ==========
    
    @Override
    @Transactional
    public Cart createCart(Cart cart) {
        return commandService.createCart(cart);
    }
    
    @Override
    @Transactional
    public void deleteCart(Long userId) {
        commandService.deleteCart(userId);
    }
    
    @Override
    @Transactional
    public void clearCart(Long userId) {
        commandService.clearCart(userId);
    }
    
    @Override
    @Transactional
    public CartItem addItemToCart(Long userId, CartItem cartItemRequest) {
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
    
    // ========== QUERY OPERATIONS ==========
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Cart> getCartByUserId(Long userId) {
        return queryService.findByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CartItem> getCartItems(Long userId) {
        return queryService.findCartItems(userId);
    }
}

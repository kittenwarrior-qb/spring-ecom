package com.example.spring_ecom.service.cart.cartItem;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.cart.CartItem;
import com.example.spring_ecom.repository.database.cart.CartEntity;
import com.example.spring_ecom.repository.database.cart.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemUseCaseService implements CartItemUseCase {
    
    private final CartItemCommandService commandService;
    private final CartItemQueryService queryService;
    private final CartRepository cartRepository;
    
    // ========== COMMAND OPERATIONS ==========
    
    @Override
    @Transactional
    public CartItem addItemToCart(Long userId, CartItem cartItemRequest) {
        CartEntity cart = getOrCreateCart(userId);
        return commandService.addItemToCart(cart, cartItemRequest);
    }
    
    @Override
    @Transactional
    public CartItem updateCartItemQuantity(Long userId, Long productId, CartItem updateRequest) {
        CartEntity cart = getCartByUserId(userId);
        return commandService.updateCartItemQuantity(cart.getId(), productId, updateRequest);
    }
    
    @Override
    @Transactional
    public void removeItemFromCart(Long userId, Long productId) {
        CartEntity cart = getCartByUserId(userId);
        commandService.removeItemFromCart(cart.getId(), productId);
    }
    
    @Override
    @Transactional
    public void clearCartItems(Long cartId) {
        commandService.clearCartItems(cartId);
    }
    
    // ========== QUERY OPERATIONS ==========
    
    @Override
    @Transactional(readOnly = true)
    public List<CartItem> findByCartId(Long cartId) {
        return queryService.findByCartId(cartId);
    }
    
    // ========== HELPER METHODS ==========
    
    private CartEntity getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Cart not found for user: " + userId));
    }
    
    private CartEntity getOrCreateCart(Long userId) {
        // This should create cart if not exists - implement based on your business logic
        return getCartByUserId(userId);
    }
}

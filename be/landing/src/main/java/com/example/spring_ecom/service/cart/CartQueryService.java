package com.example.spring_ecom.service.cart;

import com.example.spring_ecom.domain.cart.Cart;
import com.example.spring_ecom.domain.cart.CartItem;
import com.example.spring_ecom.repository.database.cart.CartEntity;
import com.example.spring_ecom.repository.database.cart.CartEntityMapper;
import com.example.spring_ecom.repository.database.cart.CartRepository;
import com.example.spring_ecom.service.cart.cartItem.CartItemUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartQueryService {
    
    private final CartRepository cartRepository;
    private final CartEntityMapper cartMapper;
    private final CartItemUseCase cartItemUseCase;
    
    // ========== MAIN QUERY METHODS ==========
    
    public Optional<Cart> findByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .map(cartMapper::toDomain);
    }
    
    public List<CartItem> findCartItems(Long userId) {
        Optional<CartEntity> cartEntity = cartRepository.findByUserId(userId);
        if (cartEntity.isEmpty()) {
            return List.of();
        }
        
        return cartItemUseCase.findByCartId(cartEntity.get().getId());
    }
    
    // ========== HELPER METHODS ==========
    
    public Optional<CartEntity> findEntityByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }
}

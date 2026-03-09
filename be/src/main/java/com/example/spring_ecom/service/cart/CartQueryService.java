package com.example.spring_ecom.service.cart;

import com.example.spring_ecom.domain.cart.Cart;
import com.example.spring_ecom.domain.cart.CartItem;
import com.example.spring_ecom.repository.database.cart.CartEntityMapper;
import com.example.spring_ecom.repository.database.cart.CartItemEntityMapper;
import com.example.spring_ecom.repository.database.cart.CartItemRepository;
import com.example.spring_ecom.repository.database.cart.CartRepository;
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
    private final CartItemRepository cartItemRepository;
    private final CartEntityMapper cartMapper;
    private final CartItemEntityMapper cartItemMapper;
    
    public Optional<Cart> findByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .map(cartMapper::toDomain);
    }
    
    public List<CartItem> findCartItems(Long cartId) {
        return cartItemRepository.findByCartId(cartId).stream()
                .map(cartItemMapper::toDomain)
                .toList();
    }
}

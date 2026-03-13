package com.example.spring_ecom.service.cart.cartItem;

import com.example.spring_ecom.domain.cart.CartItem;
import com.example.spring_ecom.repository.database.cart.cartItem.CartItemEntityMapper;
import com.example.spring_ecom.repository.database.cart.cartItem.CartItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartItemQueryService {
    
    private final CartItemRepository cartItemRepository;
    private final CartItemEntityMapper cartItemMapper;
    
    public List<CartItem> findByCartId(Long cartId) {
        return cartItemRepository.findByCartId(cartId).stream()
                .map(cartItemMapper::toDomain)
                .toList();
    }
}
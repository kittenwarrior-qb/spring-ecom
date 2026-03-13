package com.example.spring_ecom.service.cart;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.cart.Cart;
import com.example.spring_ecom.repository.database.cart.CartEntity;
import com.example.spring_ecom.repository.database.cart.CartEntityMapper;
import com.example.spring_ecom.repository.database.cart.CartRepository;
import com.example.spring_ecom.repository.database.user.UserEntity;
import com.example.spring_ecom.repository.database.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartCommandService {
    
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartEntityMapper cartMapper;
    
    public Cart createCart(Cart cart) {
        validateUser(cart.userId());
        
        CartEntity entity = cartMapper.toEntity(cart);
        CartEntity saved = cartRepository.save(entity);
        return cartMapper.toDomain(saved);
    }
    
    public void deleteCart(Long userId) {
        CartEntity cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Cart not found"));
        
        cartRepository.delete(cart);
    }
    
    private void validateUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "User not found"));
    }
}
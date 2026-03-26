package com.example.spring_ecom.service.cart;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.cart.Cart;
import com.example.spring_ecom.repository.database.cart.CartEntity;
import com.example.spring_ecom.repository.database.cart.CartEntityMapper;
import com.example.spring_ecom.repository.database.cart.CartRepository;
import com.example.spring_ecom.repository.grpc.user.UserGrpcClient;
import com.example.spring_ecom.service.cart.cartItem.CartItemUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartCommandService {
    
    private final CartRepository cartRepository;
    private final UserGrpcClient userGrpcClient;
    private final CartEntityMapper cartMapper;
    private final CartItemUseCase cartItemUseCase;
    
    // ========== MAIN COMMAND METHODS ==========
    
    public Cart createCart(Cart cart) {
        validateUser(cart.userId());
        
        CartEntity entity = cartMapper.toEntity(cart);
        CartEntity saved = cartRepository.save(entity);
        return cartMapper.toDomain(saved);
    }
    
    public void deleteCart(Long userId) {
        CartEntity cart = findCartByUserId(userId);
        
        // Clear all cart items first
        cartItemUseCase.clearCartItems(cart.getId());
        cartRepository.delete(cart);
    }
    
    public void clearCart(Long userId) {
        CartEntity cart = findCartByUserId(userId);
        cartItemUseCase.clearCartItems(cart.getId());
    }
    
    // ========== HELPER METHODS ==========
    
    private CartEntity findCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Cart not found"));
    }
    
    private void validateUser(Long userId) {
        boolean valid = userGrpcClient.validateUser(userId);
        if (!valid) {
            throw new BaseException(ResponseCode.NOT_FOUND, "User not found");
        }
    }
}
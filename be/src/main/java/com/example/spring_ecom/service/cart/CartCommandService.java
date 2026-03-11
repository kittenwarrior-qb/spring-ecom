package com.example.spring_ecom.service.cart;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.cart.CartItem;
import com.example.spring_ecom.repository.database.cart.*;
import com.example.spring_ecom.repository.database.cart.cartItem.CartItemEntity;
import com.example.spring_ecom.repository.database.cart.cartItem.CartItemEntityMapper;
import com.example.spring_ecom.repository.database.cart.cartItem.CartItemRepository;
import com.example.spring_ecom.repository.database.product.ProductEntity;
import com.example.spring_ecom.repository.database.product.ProductRepository;
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
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartItemEntityMapper cartItemMapper;
    
    public CartItem addItemToCart(Long userId, Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Quantity must be greater than 0");
        }
        
        CartEntity cart = getOrCreateCart(userId);
        ProductEntity product = productRepository.findById(productId)
                .filter(p -> p.getDeletedAt() == null && p.getIsActive())
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Product not found"));
        
        if (product.getStockQuantity() < quantity) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Insufficient stock");
        }
        
        CartItemEntity cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .map(existing -> {
                    int newQuantity = existing.getQuantity() + quantity;
                    if (product.getStockQuantity() < newQuantity) {
                        throw new BaseException(ResponseCode.BAD_REQUEST, "Insufficient stock");
                    }
                    existing.setQuantity(newQuantity);
                    return existing;
                })
                .orElseGet(() -> CartItemEntity.builder()
                        .cartId(cart.getId())
                        .productId(product.getId())
                        .quantity(quantity)
                        .price(product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice())
                        .build());
        
        CartItemEntity saved = cartItemRepository.save(cartItem);
        return cartItemMapper.toDomain(saved);
    }
    
    public CartItem updateCartItemQuantity(Long userId, Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Quantity must be greater than 0");
        }
        
        CartEntity cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Cart not found"));
        
        CartItemEntity cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Cart item not found"));
        
        ProductEntity product = productRepository.findById(cartItem.getProductId())
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Product not found"));
        
        if (product.getStockQuantity() < quantity) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Insufficient stock");
        }
        
        cartItem.setQuantity(quantity);
        CartItemEntity updated = cartItemRepository.save(cartItem);
        return cartItemMapper.toDomain(updated);
    }
    
    public void removeItemFromCart(Long userId, Long productId) {
        CartEntity cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Cart not found"));
        
        CartItemEntity cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Cart item not found"));
        
        cartItemRepository.delete(cartItem);
    }
    
    public void clearCart(Long userId) {
        CartEntity cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Cart not found"));
        
        cartItemRepository.deleteByCartId(cart.getId());
    }
    
    private CartEntity getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserEntity user = userRepository.findById(userId)
                            .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "User not found"));
                    
                    CartEntity newCart = CartEntity.builder()
                            .userId(user.getId())
                            .build();
                    return cartRepository.save(newCart);
                });
    }
}

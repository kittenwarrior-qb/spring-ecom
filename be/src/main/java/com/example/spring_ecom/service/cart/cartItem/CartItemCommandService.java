package com.example.spring_ecom.service.cart.cartItem;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.cart.CartItem;
import com.example.spring_ecom.repository.database.cart.CartEntity;
import com.example.spring_ecom.repository.database.cart.cartItem.CartItemEntity;
import com.example.spring_ecom.repository.database.cart.cartItem.CartItemEntityMapper;
import com.example.spring_ecom.repository.database.cart.cartItem.CartItemRepository;
import com.example.spring_ecom.repository.database.product.ProductEntity;
import com.example.spring_ecom.repository.database.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartItemCommandService {
    
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartItemEntityMapper cartItemMapper;
    
    public CartItem addItemToCart(CartEntity cart, CartItem cartItemRequest) {
        validateQuantity(cartItemRequest.quantity());
        ProductEntity product = validateAndGetProduct(cartItemRequest.productId(), cartItemRequest.quantity());
        
        CartItemEntity cartItem = findOrCreateCartItem(cart, cartItemRequest, product);
        CartItemEntity saved = cartItemRepository.save(cartItem);
        return cartItemMapper.toDomain(saved);
    }
    
    public CartItem updateCartItemQuantity(Long cartId, Long productId, CartItem updateRequest) {
        validateQuantity(updateRequest.quantity());
        
        CartItemEntity cartItem = cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Cart item not found"));
        
        ProductEntity product = productRepository.findById(cartItem.getProductId())
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Product not found"));
        
        if (product.getStockQuantity() < updateRequest.quantity()) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Insufficient stock");
        }
        
        cartItemMapper.update(cartItem, updateRequest);
        
        CartItemEntity updated = cartItemRepository.save(cartItem);
        return cartItemMapper.toDomain(updated);
    }
    
    public void removeItemFromCart(Long cartId, Long productId) {
        CartItemEntity cartItem = cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Cart item not found"));
        
        cartItemRepository.delete(cartItem);
    }
    
    public void clearCartItems(Long cartId) {
        cartItemRepository.deleteByCartId(cartId);
    }
    
    private void validateQuantity(Integer quantity) {
        if (quantity <= 0) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Quantity must be greater than 0");
        }
    }
    
    private ProductEntity validateAndGetProduct(Long productId, Integer quantity) {
        ProductEntity product = productRepository.findById(productId)
                .filter(p -> Objects.isNull(p.getDeletedAt()) && p.getIsActive())
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Product not found"));
        
        validateStockForQuantity(product, quantity);
        return product;
    }
    
    private void validateStockForQuantity(ProductEntity product, Integer quantity) {
        if (product.getStockQuantity() < quantity) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Insufficient stock");
        }
    }
    
    private CartItemEntity findOrCreateCartItem(CartEntity cart, CartItem request, ProductEntity product) {
        return cartItemRepository.findByCartIdAndProductId(cart.getId(), request.productId())
                .map(existing -> updateExistingItem(existing, request, product))
                .orElseGet(() -> createNewItem(request, cart.getId(), product));
    }
    
    private CartItemEntity updateExistingItem(CartItemEntity existing, CartItem request, ProductEntity product) {
        int newQuantity = existing.getQuantity() + request.quantity();
        validateStockForQuantity(product, newQuantity);
        existing.setQuantity(newQuantity);
        return existing;
    }
    
    private CartItemEntity createNewItem(CartItem request, Long cartId, ProductEntity product) {
        CartItem enrichedRequest = enrichCartItemRequest(request, cartId, product);
        return cartItemMapper.toEntity(enrichedRequest);
    }
    
    private CartItem enrichCartItemRequest(CartItem request, Long cartId, ProductEntity product) {
        BigDecimal price = Objects.nonNull(product.getDiscountPrice()) ? 
                product.getDiscountPrice() : product.getPrice();
                
        return new CartItem(
                request.id(),
                cartId,
                request.productId(),
                request.quantity(),
                price,
                request.createdAt(),
                request.updatedAt()
        );
    }
}
package com.example.spring_ecom.service.cart.cartItem;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.cart.CartItem;
import com.example.spring_ecom.domain.product.Product;
import com.example.spring_ecom.repository.database.cart.CartEntity;
import com.example.spring_ecom.repository.database.cart.cartItem.CartItemEntity;
import com.example.spring_ecom.repository.database.cart.cartItem.CartItemEntityMapper;
import com.example.spring_ecom.repository.database.cart.cartItem.CartItemRepository;
import com.example.spring_ecom.service.product.ProductUseCase;
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
    private final ProductUseCase productUseCase;
    private final CartItemEntityMapper cartItemMapper;
    
    // ========================== MAIN METHODS ================================

    public CartItem addItemToCart(CartEntity cart, CartItem cartItemRequest) {
        validateQuantity(cartItemRequest.quantity());
        Product product = validateAndGetProduct(cartItemRequest.productId(), cartItemRequest.quantity());
        
        CartItemEntity cartItem = findOrCreateCartItem(cart, cartItemRequest, product);
        CartItemEntity saved = cartItemRepository.save(cartItem);
        return cartItemMapper.toDomain(saved);
    }
    
    public CartItem updateCartItemQuantity(Long cartId, Long productId, CartItem updateRequest) {
        validateQuantity(updateRequest.quantity());
        
        CartItemEntity cartItem = cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Cart item not found"));
        
        Product product = productUseCase.findById(cartItem.getProductId())
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Product not found"));
        
        if (product.stockQuantity() < updateRequest.quantity()) {
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
    

    // ========================== SUPPORT METHODS ================================

    private Product validateAndGetProduct(Long productId, Integer quantity) {
        Product product = productUseCase.findById(productId)
                .filter(p -> p.isActive())
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Product not found"));
        
        validateStockForQuantity(product, quantity);
        return product;
    }
    
    private void validateStockForQuantity(Product product, Integer quantity) {
        if (product.stockQuantity() < quantity) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Insufficient stock");
        }
    }
    
    private CartItemEntity findOrCreateCartItem(CartEntity cart, CartItem request, Product product) {
        return cartItemRepository.findByCartIdAndProductId(cart.getId(), request.productId())
                .map(existing -> updateExistingItem(existing, request, product))
                .orElseGet(() -> createNewItem(request, cart.getId(), product));
    }
    
    private CartItemEntity updateExistingItem(CartItemEntity existing, CartItem request, Product product) {
        int newQuantity = existing.getQuantity() + request.quantity();
        validateStockForQuantity(product, newQuantity);
        existing.setQuantity(newQuantity);
        return existing;
    }
    
    private CartItemEntity createNewItem(CartItem request, Long cartId, Product product) {
        CartItem enrichedRequest = enrichCartItemRequest(request, cartId, product);
        return cartItemMapper.toEntity(enrichedRequest);
    }
    
    private CartItem enrichCartItemRequest(CartItem request, Long cartId, Product product) {
        BigDecimal price = Objects.nonNull(product.discountPrice()) ?
                product.discountPrice() : product.price();

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
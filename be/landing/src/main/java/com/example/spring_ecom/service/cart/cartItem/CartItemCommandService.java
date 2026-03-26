package com.example.spring_ecom.service.cart.cartItem;

import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.cart.CartItem;
import com.example.spring_ecom.grpc.domain.ProductProto;
import com.example.spring_ecom.repository.database.cart.CartEntity;
import com.example.spring_ecom.repository.database.cart.cartItem.CartItemEntity;
import com.example.spring_ecom.repository.database.cart.cartItem.CartItemEntityMapper;
import com.example.spring_ecom.repository.database.cart.cartItem.CartItemRepository;
import com.example.spring_ecom.repository.grpc.product.ProductGrpcClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartItemCommandService {
    
    private final CartItemRepository cartItemRepository;
    private final ProductGrpcClient productGrpcClient;
    private final CartItemEntityMapper cartItemMapper;
    
    // ========================== MAIN METHODS ================================

    public CartItem addItemToCart(CartEntity cart, CartItem cartItemRequest) {
        validateQuantity(cartItemRequest.quantity());
        ProductProto.Product product = validateAndGetProduct(cartItemRequest.productId(), cartItemRequest.quantity());
        
        CartItemEntity cartItem = findOrCreateCartItem(cart, cartItemRequest, product);
        CartItemEntity saved = cartItemRepository.save(cartItem);
        return cartItemMapper.toDomain(saved);
    }
    
    public CartItem updateCartItemQuantity(Long cartId, Long productId, CartItem updateRequest) {
        validateQuantity(updateRequest.quantity());
        
        CartItemEntity cartItem = cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Cart item not found"));
        
        // Check stock via gRPC
        boolean available = productGrpcClient.validateProductAvailability(productId, updateRequest.quantity());
        if (!available) {
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
    
    @Transactional
    public void clearCartItems(Long cartId) {
        cartItemRepository.deleteByCartId(cartId);
    }

    // ========================== SUPPORT METHODS ================================

    private ProductProto.Product validateAndGetProduct(Long productId, Integer quantity) {
        ProductProto.Product product = productGrpcClient.getProductById(productId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND, "Product not found"));
        
        if (!product.getIsActive()) {
            throw new BaseException(ResponseCode.NOT_FOUND, "Product not found or inactive");
        }

        boolean available = productGrpcClient.validateProductAvailability(productId, quantity);
        if (!available) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Insufficient stock");
        }
        return product;
    }
    
    private void validateQuantity(Integer quantity) {
        if (quantity <= 0) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Quantity must be greater than 0");
        }
    }

    private CartItemEntity findOrCreateCartItem(CartEntity cart, CartItem request, ProductProto.Product product) {
        return cartItemRepository.findByCartIdAndProductId(cart.getId(), request.productId())
                .map(existing -> updateExistingItem(existing, request, product))
                .orElseGet(() -> createNewItem(request, cart.getId(), product));
    }
    
    private CartItemEntity updateExistingItem(CartItemEntity existing, CartItem request, ProductProto.Product product) {
        int newQuantity = existing.getQuantity() + request.quantity();
        boolean available = productGrpcClient.validateProductAvailability(request.productId(), newQuantity);
        if (!available) {
            throw new BaseException(ResponseCode.BAD_REQUEST, "Insufficient stock for total quantity: " + newQuantity);
        }
        existing.setQuantity(newQuantity);
        return existing;
    }
    
    private CartItemEntity createNewItem(CartItem request, Long cartId, ProductProto.Product product) {
        CartItem enrichedRequest = enrichCartItemRequest(request, cartId, product);
        return cartItemMapper.toEntity(enrichedRequest);
    }
    
    private CartItem enrichCartItemRequest(CartItem request, Long cartId, ProductProto.Product product) {
        BigDecimal price = product.getDiscountPrice() > 0
                ? BigDecimal.valueOf(product.getDiscountPrice())
                : BigDecimal.valueOf(product.getPrice());
                
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
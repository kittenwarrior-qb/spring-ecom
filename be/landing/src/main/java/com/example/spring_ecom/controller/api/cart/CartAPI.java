package com.example.spring_ecom.controller.api.cart;

import com.example.spring_ecom.controller.api.cart.model.AddToCartRequest;
import com.example.spring_ecom.controller.api.cart.model.CartItemResponse;
import com.example.spring_ecom.controller.api.cart.model.UpdateCartItemRequest;
import com.example.spring_ecom.core.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Cart", description = "Shopping cart management APIs")
@RequestMapping("/api/cart")
public interface CartAPI {
    
    @Operation(summary = "Get cart items for current user")
    @GetMapping
    ApiResponse<List<CartItemResponse>> getCartItems();
    
    @Operation(summary = "Add item to cart")
    @PostMapping("/items")
    ApiResponse<CartItemResponse> addItemToCart(@Valid @RequestBody AddToCartRequest request);
    
    @Operation(summary = "Update cart item quantity")
    @PutMapping("/items/{productId}")
    ApiResponse<CartItemResponse> updateCartItemQuantity(
            @PathVariable Long productId, 
            @Valid @RequestBody UpdateCartItemRequest request);
    
    @Operation(summary = "Remove item from cart")
    @DeleteMapping("/items/{productId}")
    ApiResponse<Void> removeItemFromCart(@PathVariable Long productId);
    
    @Operation(summary = "Clear all items from cart")
    @DeleteMapping
    ApiResponse<Void> clearCart();

    @Operation(summary = "Sync local cart to server")
    @PostMapping("/sync")
    ApiResponse<Void> syncCart(@RequestBody List<AddToCartRequest> items);
}


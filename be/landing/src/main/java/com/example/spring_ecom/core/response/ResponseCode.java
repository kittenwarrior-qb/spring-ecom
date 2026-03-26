package com.example.spring_ecom.core.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {
    
    // 2xx Success responses
    OK(200, "Success"),
    CREATED(201, "Resource created successfully"),
    ACCEPTED(202, "Request accepted"),
    NO_CONTENT(204, "No content"),
    
    // 4xx Client errors
    BAD_REQUEST(400, "Bad request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Resource not found"),
    ENDPOINT_NOT_FOUND(404, "API endpoint not found"),
    CONFLICT(409, "Resource already exists"),
    TOO_MANY_REQUESTS(429, "Too many requests"),
    
    // User
    USER_GET(200, "User retrieved successfully"),
    USER_LIST(200, "Users retrieved successfully"),
    USER_CREATED(201, "User created successfully"),
    USER_UPDATED(200, "User updated successfully"),
    USER_DELETED(200, "User deleted successfully"),
    USER_NOT_FOUND(404, "User not found"),
    USER_ALREADY_EXISTS(409, "User already exists"),
    INVALID_CREDENTIALS(401, "Invalid credentials"),
    USER_DISABLED(403, "User account is disabled"),
    
    // Product
    PRODUCT_CREATED(201, "Product created successfully"),
    PRODUCT_UPDATED(200, "Product updated successfully"),
    PRODUCT_DELETED(200, "Product deleted successfully"),
    PRODUCT_NOT_FOUND(404, "Product not found"),
    PRODUCT_OUT_OF_STOCK(400, "Product is out of stock"),
    INVALID_PRODUCT_DATA(400, "Invalid product data"),
    
    // Order
    ORDER_CREATED(201, "Order created successfully"),
    ORDER_UPDATED(200, "Order updated successfully"),
    ORDER_CANCELLED(200, "Order cancelled successfully"),
    ORDER_NOT_FOUND(404, "Order not found"),
    ORDER_ALREADY_PROCESSED(409, "Order already processed"),
    INVALID_ORDER_STATUS(400, "Invalid order status"),
    
    // Payment
    PAYMENT_PROCESSED(200, "Payment processed successfully"),
    PAYMENT_FAILED(400, "Payment failed"),
    INVALID_PAYMENT_METHOD(400, "Invalid payment method"),
    INSUFFICIENT_FUNDS(400, "Insufficient funds"),
    
    // Server errors
    INTERNAL_SERVER_ERROR(500, "Internal server error");
    
    private final int code;
    private final String message;
}

package com.example.spring_ecom.controller.api.order.orderItem.model;

public record PartialCancelRequestItem(
    Long orderItemId,
    Integer quantityToCancel
) {
}
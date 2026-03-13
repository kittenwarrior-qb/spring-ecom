package com.example.spring_ecom.controller.api.order.model;

import java.util.List;

public record PartialCancelRequest(
    List<PartialCancelItem> items
) {
    public record PartialCancelItem(
        Long orderItemId,
        Integer quantityToCancel
    ) {}
}
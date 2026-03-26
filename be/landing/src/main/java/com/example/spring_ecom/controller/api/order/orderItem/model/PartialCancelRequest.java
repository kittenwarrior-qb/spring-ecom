package com.example.spring_ecom.controller.api.order.orderItem.model;

import java.util.List;

public record PartialCancelRequest(
    List<PartialCancelRequestItem> items
) {
}
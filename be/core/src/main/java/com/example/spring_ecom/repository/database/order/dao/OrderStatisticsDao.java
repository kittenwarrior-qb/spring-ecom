package com.example.spring_ecom.repository.database.order.dao;

import java.math.BigDecimal;

/**
 * Interface projection for native query result
 * Spring Data JPA will auto-map column aliases to getter methods
 */
public interface OrderStatisticsDao {
    Long getTotalOrders();
    Long getPendingOrders();
    Long getConfirmedOrders();
    Long getShippedOrders();
    Long getDeliveredOrders();
    Long getCancelledOrders();
    Long getPartiallyCancelledOrders();
    BigDecimal getTotalRevenue();
    BigDecimal getTodayRevenue();
}
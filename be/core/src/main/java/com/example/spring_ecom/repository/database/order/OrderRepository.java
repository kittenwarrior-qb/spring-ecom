package com.example.spring_ecom.repository.database.order;

import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.repository.database.order.dao.OrderStatisticsDao;
import com.example.spring_ecom.repository.database.order.dao.OrderWithUserDao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    
    Optional<OrderEntity> findByOrderNumber(String orderNumber);
    
    @Query("""
        SELECT new com.example.spring_ecom.repository.database.order.dao.OrderWithUserDao(
               o.id, o.orderNumber, o.userId, u.email,
               o.status, o.paymentStatus, o.subtotal,
               o.shippingFee, o.discount, o.total, o.refundedAmount,
               CAST(o.paymentMethod AS string), o.shippingAddress,
               o.shippingCity, o.shippingDistrict,
               o.shippingWard, o.recipientName,
               o.recipientPhone, o.note,
               o.createdAt, o.updatedAt, o.cancelledAt)
        FROM OrderEntity o 
        JOIN UserEntity u ON o.userId = u.id 
        WHERE o.id = :id
    """)
    Optional<OrderWithUserDao> findOrderWithUserById(@Param("id") Long id);
    
    @Query("""
        SELECT new com.example.spring_ecom.repository.database.order.dao.OrderWithUserDao(
               o.id, o.orderNumber, o.userId, u.email,
               o.status, o.paymentStatus, o.subtotal,
               o.shippingFee, o.discount, o.total, o.refundedAmount,
               CAST(o.paymentMethod AS string), o.shippingAddress,
               o.shippingCity, o.shippingDistrict,
               o.shippingWard, o.recipientName,
               o.recipientPhone, o.note,
               o.createdAt, o.updatedAt, o.cancelledAt)
        FROM OrderEntity o 
        JOIN UserEntity u ON o.userId = u.id 
        ORDER BY o.createdAt DESC
    """)
    Page<OrderWithUserDao> findAllOrdersWithUser(Pageable pageable);
    
    // Unified method for filtering orders
    @Query("""
        SELECT o FROM OrderEntity o 
        WHERE (:userId IS NULL OR o.userId = :userId)
        AND (:status IS NULL OR o.status = :status)
        ORDER BY o.createdAt DESC
    """)
    Page<OrderEntity> findOrdersWithFilters(
        @Param("userId") Long userId,
        @Param("status") OrderStatus status,
        Pageable pageable
    );
    
    // Convenience methods for backward compatibility
    default Page<OrderEntity> findByUserId(Long userId, Pageable pageable) {
        return findOrdersWithFilters(userId, null, pageable);
    }
    
    default Page<OrderEntity> findByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable) {
        return findOrdersWithFilters(userId, status, pageable);
    }
    
    default Page<OrderEntity> findByStatus(OrderStatus status, Pageable pageable) {
        return findOrdersWithFilters(null, status, pageable);
    }
    
    boolean existsByOrderNumber(String orderNumber);
    
    /**
     * Find all orders ordered by createdAt DESC (newest first)
     */
    @Query("SELECT o FROM OrderEntity o ORDER BY o.createdAt DESC")
    Page<OrderEntity> findAllOrderByCreatedAtDesc(Pageable pageable);
    
    // ========== Optimized Statistics Queries ==========
    
    /**
     * Count orders by status in a single native query
     */
    @Query(value = """
        SELECT 
            COUNT(*) as totalOrders,
            COALESCE(SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END), 0) as pendingOrders,
            COALESCE(SUM(CASE WHEN status = 'CONFIRMED' THEN 1 ELSE 0 END), 0) as confirmedOrders,
            COALESCE(SUM(CASE WHEN status = 'SHIPPED' THEN 1 ELSE 0 END), 0) as shippedOrders,
            COALESCE(SUM(CASE WHEN status = 'DELIVERED' THEN 1 ELSE 0 END), 0) as deliveredOrders,
            COALESCE(SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END), 0) as cancelledOrders,
            COALESCE(SUM(CASE WHEN status = 'PARTIALLY_CANCELLED' THEN 1 ELSE 0 END), 0) as partiallyCancelledOrders,
            COALESCE(SUM(CASE WHEN status = 'DELIVERED' THEN total ELSE 0 END), 0) as totalRevenue,
            0 as todayRevenue
        FROM orders
        """, nativeQuery = true)
    OrderStatisticsDao getOrderStatistics();
    
    /**
     * Get today's revenue in a single query
     */
    @Query("""
        SELECT COALESCE(SUM(o.total), 0)
        FROM OrderEntity o
        WHERE o.status = 'DELIVERED'
        AND o.createdAt >= :startOfDay
        AND o.createdAt <= :endOfDay
        """)
    BigDecimal getTodayRevenue(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
    
    // ========== Date-Range Statistics Queries ==========
    
    /**
     * Count orders by status within a date range
     */
    @Query(value = """
        SELECT 
            COUNT(*) as totalOrders,
            COALESCE(SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END), 0) as pendingOrders,
            COALESCE(SUM(CASE WHEN status = 'CONFIRMED' THEN 1 ELSE 0 END), 0) as confirmedOrders,
            COALESCE(SUM(CASE WHEN status = 'SHIPPED' THEN 1 ELSE 0 END), 0) as shippedOrders,
            COALESCE(SUM(CASE WHEN status = 'DELIVERED' THEN 1 ELSE 0 END), 0) as deliveredOrders,
            COALESCE(SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END), 0) as cancelledOrders,
            COALESCE(SUM(CASE WHEN status = 'PARTIALLY_CANCELLED' THEN 1 ELSE 0 END), 0) as partiallyCancelledOrders,
            COALESCE(SUM(CASE WHEN status = 'DELIVERED' THEN total ELSE 0 END), 0) as totalRevenue,
            0 as todayRevenue
        FROM orders
        WHERE created_at >= :dateFrom AND created_at <= :dateTo
        """, nativeQuery = true)
    OrderStatisticsDao getOrderStatisticsInRange(
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo
    );
    
    /**
     * Daily revenue breakdown (for charts)
     */
    @Query(value = """
        SELECT 
            DATE(created_at) as statDate,
            COUNT(*) as orderCount,
            COALESCE(SUM(CASE WHEN status = 'DELIVERED' THEN total ELSE 0 END), 0) as revenue
        FROM orders
        WHERE created_at >= :dateFrom AND created_at <= :dateTo
        GROUP BY DATE(created_at)
        ORDER BY DATE(created_at)
        """, nativeQuery = true)
    List<Object[]> getDailyStatistics(
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo
    );
    
    /**
     * Revenue with cost (profit) for delivered orders in a date range
     * Uses (quantity - cancelled_quantity) * price for accurate revenue after partial cancellations
     * Uses order_items.cost_price (FIFO-based) when available, falls back to products.cost_price
     */
    @Query(value = """
        SELECT 
            COALESCE(SUM((oi.quantity - oi.cancelled_quantity) * oi.price), 0) as totalRevenue,
            COALESCE(SUM((oi.quantity - oi.cancelled_quantity) * COALESCE(oi.cost_price, p.cost_price, 0)), 0) as totalCost,
            COALESCE(SUM((oi.quantity - oi.cancelled_quantity) * oi.price - (oi.quantity - oi.cancelled_quantity) * COALESCE(oi.cost_price, p.cost_price, 0)), 0) as totalProfit
        FROM orders o
        JOIN order_items oi ON o.id = oi.order_id
        JOIN products p ON oi.product_id = p.id
        WHERE o.status = 'DELIVERED'
        AND o.created_at >= :dateFrom AND o.created_at <= :dateTo
        """, nativeQuery = true)
    Object[] getRevenueCostProfit(
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo
    );
    
    /**
     * Top selling products in a date range
     */
    @Query(value = """
        SELECT 
            p.id as productId,
            p.title as productTitle,
            SUM(oi.quantity - oi.cancelled_quantity) as totalSold,
            SUM((oi.quantity - oi.cancelled_quantity) * oi.price) as totalRevenue
        FROM orders o
        JOIN order_items oi ON o.id = oi.order_id
        JOIN products p ON oi.product_id = p.id
        WHERE o.status = 'DELIVERED'
        AND o.created_at >= :dateFrom AND o.created_at <= :dateTo
        GROUP BY p.id, p.title
        ORDER BY totalSold DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> getTopSellingProducts(
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo,
        @Param("limit") int limit
    );

    // ========== Revenue by Category ==========

    /**
     * Revenue breakdown by category for delivered orders in a date range
     */
    @Query(value = """
        SELECT 
            c.id as categoryId,
            c.name as categoryName,
            COUNT(DISTINCT o.id) as orderCount,
            SUM((oi.quantity - oi.cancelled_quantity) * oi.price) as totalRevenue,
            SUM((oi.quantity - oi.cancelled_quantity) * COALESCE(oi.cost_price, p.cost_price, 0)) as totalCost,
            SUM((oi.quantity - oi.cancelled_quantity) * oi.price - (oi.quantity - oi.cancelled_quantity) * COALESCE(oi.cost_price, p.cost_price, 0)) as totalProfit
        FROM orders o
        JOIN order_items oi ON o.id = oi.order_id
        JOIN products p ON oi.product_id = p.id
        LEFT JOIN categories c ON p.category_id = c.id
        WHERE o.status = 'DELIVERED'
        AND o.created_at >= :dateFrom AND o.created_at <= :dateTo
        GROUP BY c.id, c.name
        ORDER BY totalRevenue DESC
        """, nativeQuery = true)
    List<Object[]> getRevenueByCategoryInRange(
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo
    );

    // ========== Daily Profit Breakdown ==========

    /**
     * Daily profit breakdown (revenue, cost, profit per day) for charts
     */
    @Query(value = """
        SELECT 
            DATE(o.created_at) as statDate,
            COUNT(DISTINCT o.id) as orderCount,
            COALESCE(SUM((oi.quantity - oi.cancelled_quantity) * oi.price), 0) as revenue,
            COALESCE(SUM((oi.quantity - oi.cancelled_quantity) * COALESCE(oi.cost_price, p.cost_price, 0)), 0) as cost,
            COALESCE(SUM((oi.quantity - oi.cancelled_quantity) * oi.price - (oi.quantity - oi.cancelled_quantity) * COALESCE(oi.cost_price, p.cost_price, 0)), 0) as profit
        FROM orders o
        JOIN order_items oi ON o.id = oi.order_id
        JOIN products p ON oi.product_id = p.id
        WHERE o.status = 'DELIVERED'
        AND o.created_at >= :dateFrom AND o.created_at <= :dateTo
        GROUP BY DATE(o.created_at)
        ORDER BY DATE(o.created_at)
        """, nativeQuery = true)
    List<Object[]> getDailyProfitBreakdown(
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo
    );

    /**
     * Weekly profit breakdown
     */
    @Query(value = """
        SELECT 
            DATE_TRUNC('week', o.created_at)::date as statDate,
            COUNT(DISTINCT o.id) as orderCount,
            COALESCE(SUM((oi.quantity - oi.cancelled_quantity) * oi.price), 0) as revenue,
            COALESCE(SUM((oi.quantity - oi.cancelled_quantity) * COALESCE(oi.cost_price, p.cost_price, 0)), 0) as cost,
            COALESCE(SUM((oi.quantity - oi.cancelled_quantity) * oi.price - (oi.quantity - oi.cancelled_quantity) * COALESCE(oi.cost_price, p.cost_price, 0)), 0) as profit
        FROM orders o
        JOIN order_items oi ON o.id = oi.order_id
        JOIN products p ON oi.product_id = p.id
        WHERE o.status = 'DELIVERED'
        AND o.created_at >= :dateFrom AND o.created_at <= :dateTo
        GROUP BY DATE_TRUNC('week', o.created_at)
        ORDER BY DATE_TRUNC('week', o.created_at)
        """, nativeQuery = true)
    List<Object[]> getWeeklyProfitBreakdown(
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo
    );

    /**
     * Monthly profit breakdown
     */
    @Query(value = """
        SELECT 
            DATE_TRUNC('month', o.created_at)::date as statDate,
            COUNT(DISTINCT o.id) as orderCount,
            COALESCE(SUM((oi.quantity - oi.cancelled_quantity) * oi.price), 0) as revenue,
            COALESCE(SUM((oi.quantity - oi.cancelled_quantity) * COALESCE(oi.cost_price, p.cost_price, 0)), 0) as cost,
            COALESCE(SUM((oi.quantity - oi.cancelled_quantity) * oi.price - (oi.quantity - oi.cancelled_quantity) * COALESCE(oi.cost_price, p.cost_price, 0)), 0) as profit
        FROM orders o
        JOIN order_items oi ON o.id = oi.order_id
        JOIN products p ON oi.product_id = p.id
        WHERE o.status = 'DELIVERED'
        AND o.created_at >= :dateFrom AND o.created_at <= :dateTo
        GROUP BY DATE_TRUNC('month', o.created_at)
        ORDER BY DATE_TRUNC('month', o.created_at)
        """, nativeQuery = true)
    List<Object[]> getMonthlyProfitBreakdown(
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo
    );

    /**
     * Average order value for delivered orders in a date range
     */
    @Query(value = """
        SELECT COALESCE(AVG(total), 0)
        FROM orders
        WHERE status = 'DELIVERED'
        AND created_at >= :dateFrom AND created_at <= :dateTo
        """, nativeQuery = true)
    BigDecimal getAverageOrderValue(
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo
    );
}

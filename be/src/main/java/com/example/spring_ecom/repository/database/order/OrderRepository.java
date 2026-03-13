package com.example.spring_ecom.repository.database.order;

import com.example.spring_ecom.domain.order.OrderStatus;
import com.example.spring_ecom.repository.database.order.dao.OrderWithUserDao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    
    Optional<OrderEntity> findByOrderNumber(String orderNumber);
    
    @Query("""
        SELECT new com.example.spring_ecom.service.order.dao.OrderWithUserDao(
               o.id, o.orderNumber, o.userId, u.email,
               o.status, o.paymentStatus, o.subtotal,
               o.shippingFee, o.discount, o.total,
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
}

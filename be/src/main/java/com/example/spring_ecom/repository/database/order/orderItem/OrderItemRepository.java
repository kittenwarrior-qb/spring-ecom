package com.example.spring_ecom.repository.database.order.orderItem;

import com.example.spring_ecom.service.order.dao.OrderItemWithProductDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    
    @Query("SELECT oi FROM OrderItemEntity oi WHERE oi.orderId = :orderId")
    List<OrderItemEntity> findByOrderId(@Param("orderId") Long orderId);
    
    @Query("SELECT oi FROM OrderItemEntity oi WHERE oi.productId = :productId")
    List<OrderItemEntity> findByProductId(@Param("productId") Long productId);
    
    @Query("SELECT oi FROM OrderItemEntity oi WHERE oi.orderId IN :orderIds")
    List<OrderItemEntity> findByOrderIdIn(@Param("orderIds") List<Long> orderIds);
    
    @Query("""
        SELECT new com.example.spring_ecom.service.order.dao.OrderItemWithProductDao(
               oi.id, oi.orderId, oi.productId,
               oi.productTitle, p.coverImageUrl,
               oi.quantity, oi.price, oi.subtotal,
               oi.createdAt)
        FROM OrderItemEntity oi 
        JOIN ProductEntity p ON oi.productId = p.id 
        WHERE oi.orderId = :orderId
    """)
    List<OrderItemWithProductDao> findOrderItemsWithProductByOrderId(@Param("orderId") Long orderId);
}
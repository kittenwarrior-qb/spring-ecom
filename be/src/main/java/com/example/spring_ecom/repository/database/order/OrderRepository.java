package com.example.spring_ecom.repository.database.order;

import com.example.spring_ecom.domain.order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    
    Optional<OrderEntity> findByOrderNumber(String orderNumber);
    
    Page<OrderEntity> findByUserId(Long userId, Pageable pageable);
    
    Page<OrderEntity> findByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable);
    
    Page<OrderEntity> findByStatus(OrderStatus status, Pageable pageable);
    
    boolean existsByOrderNumber(String orderNumber);
}

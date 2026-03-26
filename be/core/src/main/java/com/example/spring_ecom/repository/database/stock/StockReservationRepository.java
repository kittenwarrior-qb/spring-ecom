package com.example.spring_ecom.repository.database.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockReservationRepository extends JpaRepository<StockReservationEntity, Long> {
    
    List<StockReservationEntity> findByOrderId(Long orderId);
    
    List<StockReservationEntity> findByOrderIdAndStatus(Long orderId, StockReservationEntity.ReservationStatus status);
    
    List<StockReservationEntity> findByStatusAndExpireAtBefore(
            StockReservationEntity.ReservationStatus status, 
            Instant expireAt);
    
    @Query("SELECT r FROM StockReservationEntity r WHERE r.status = 'ACTIVE' AND r.expireAt < :now")
    List<StockReservationEntity> findExpiredReservations(@Param("now") Instant now);
    
    @Modifying
    @Query("UPDATE StockReservationEntity r SET r.status = :status, r.releasedAt = :releasedAt WHERE r.orderId = :orderId AND r.status = 'ACTIVE'")
    int updateStatusByOrderId(@Param("orderId") Long orderId, 
                              @Param("status") StockReservationEntity.ReservationStatus status,
                              @Param("releasedAt") Instant releasedAt);
    
    @Modifying
    @Query("UPDATE StockReservationEntity r SET r.status = :status, r.releasedAt = :releasedAt WHERE r.id = :id AND r.status = 'ACTIVE'")
    int updateStatusById(@Param("id") Long id,
                         @Param("status") StockReservationEntity.ReservationStatus status,
                         @Param("releasedAt") Instant releasedAt);
    
    void deleteByOrderId(Long orderId);
}

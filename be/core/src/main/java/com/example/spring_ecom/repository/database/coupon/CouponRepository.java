package com.example.spring_ecom.repository.database.coupon;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
    
    Optional<CouponEntity> findByCodeAndDeletedAtIsNull(String code);
    
    Optional<CouponEntity> findByIdAndDeletedAtIsNull(Long id);
    
    Page<CouponEntity> findByDeletedAtIsNull(Pageable pageable);
    
    @Query("""
        SELECT c FROM CouponEntity c
        WHERE c.deletedAt IS NULL
        AND c.isActive = true
        AND c.startDate <= :now
        AND c.endDate >= :now
        AND (c.usageLimit IS NULL OR c.usedCount < c.usageLimit)
        """)
    Page<CouponEntity> findActiveCoupons(@Param("now") LocalDateTime now, Pageable pageable);
    
    @Query("""
        SELECT c FROM CouponEntity c
        WHERE c.code = :code
        AND c.deletedAt IS NULL
        AND c.isActive = true
        AND c.startDate <= :now
        AND c.endDate >= :now
        AND (c.usageLimit IS NULL OR c.usedCount < c.usageLimit)
        """)
    Optional<CouponEntity> findValidCouponByCode(@Param("code") String code, @Param("now") LocalDateTime now);
    
    boolean existsByCodeAndDeletedAtIsNull(String code);
    
    // ========== PESSIMISTIC LOCK METHODS (Prevent Race Condition) ==========
    
    /**
     * Find coupon with pessimistic write lock for usage operations
     * Use this for incrementUsage operations
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CouponEntity c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<CouponEntity> findByIdWithLock(@Param("id") Long id);
}

package com.example.spring_ecom.service.coupon;

import com.example.spring_ecom.domain.coupon.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponUseCaseService implements CouponUseCase {
    
    private final CouponQueryService queryService;
    private final CouponCommandService commandService;
    
    @Override
    @Transactional(readOnly = true)
    public Page<Coupon> findAll(Pageable pageable) {
        return queryService.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Coupon> findActiveCoupons(Pageable pageable) {
        return queryService.findActiveCoupons(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Coupon> findById(Long id) {
        return queryService.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Coupon> findByCode(String code) {
        return queryService.findByCode(code);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<CouponValidationResult> validateCoupon(String code, BigDecimal orderTotal) {
        CouponValidationResult result = queryService.validateCoupon(code, orderTotal);
        return result.valid() ? Optional.of(result) : Optional.empty();
    }
    
    @Override
    @Transactional
    public Optional<Coupon> create(Coupon coupon) {
        return commandService.create(coupon);
    }
    
    @Override
    @Transactional
    public Optional<Coupon> createWithNotification(Coupon coupon, NotificationType notificationType, List<Long> targetUserIds) {
        return commandService.createWithNotification(coupon, notificationType, targetUserIds);
    }
    
    @Override
    @Transactional
    public Optional<Coupon> update(Long id, Coupon coupon) {
        return commandService.update(id, coupon);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        commandService.delete(id);
    }
    
    @Override
    public void incrementUsage(Long couponId) {
        commandService.incrementUsage(couponId);
    }
}

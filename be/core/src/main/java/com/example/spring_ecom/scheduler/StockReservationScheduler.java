package com.example.spring_ecom.scheduler;

import com.example.spring_ecom.repository.database.stock.StockReservationEntity;
import com.example.spring_ecom.repository.database.stock.StockReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Scheduler để release expired stock reservations
 * Chạy mỗi phút để check reservations đã hết hạn
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockReservationScheduler {
    
    private final StockReservationRepository reservationRepository;
    private final StockReleaseTransactionService stockReleaseTransactionService;

    /**
     * Run every minute to release expired reservations
     */
    @Scheduled(fixedRate = 60000)
    public void releaseExpiredReservations() {
        Instant now = Instant.now();
        List<StockReservationEntity> expiredReservations = reservationRepository.findExpiredReservations(now);
        
        if (expiredReservations.isEmpty()) {
            return;
        }
        
        log.info("[SCHEDULER] Found {} expired stock reservations", expiredReservations.size());
        
        for (StockReservationEntity reservation : expiredReservations) {
            try {
                stockReleaseTransactionService.releaseOneReservation(reservation);
            } catch (Exception e) {
                log.error("Failed to release reservation: id={}, orderId={}", 
                        reservation.getId(), reservation.getOrderId(), e);
            }
        }
    }
}

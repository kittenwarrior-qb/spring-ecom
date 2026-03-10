package com.example.spring_ecom.controller.api.payment;

import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.PageResponse;
import com.example.spring_ecom.domain.order.SepayTransaction;
import com.example.spring_ecom.repository.database.SepayTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("v1/api/sepay")
@RequiredArgsConstructor
public class SepayTransactionController {
    
    private final SepayTransactionRepository sepayTransactionRepository;
    
    @GetMapping("/transactions")
    public ApiResponse<PageResponse<SepayTransaction>> getTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        int offset = (page - 1) * limit;
        
        List<SepayTransaction> transactions = sepayTransactionRepository.findAll(limit, offset);
        long total = sepayTransactionRepository.count();
        
        PageResponse<SepayTransaction> pageResponse = new PageResponse<>(
                transactions,
                page,
                limit,
                total,
                (int) Math.ceil((double) total / limit)
        );
        
        return ApiResponse.Success.of(pageResponse);
    }
}
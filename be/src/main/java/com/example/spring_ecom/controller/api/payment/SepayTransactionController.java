package com.example.spring_ecom.controller.api.payment;

import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.domain.order.SepayTransaction;
import com.example.spring_ecom.repository.database.SepayTransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("v1/api/sepay")
@RequiredArgsConstructor
public class SepayTransactionController {
    
    private final SepayTransactionRepository sepayTransactionRepository;
    
    @GetMapping("/transactions")
    public ApiResponse<Map<String, Object>> getTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        int offset = (page - 1) * limit;
        
        List<SepayTransaction> transactions = sepayTransactionRepository.findAll(limit, offset);
        long total = sepayTransactionRepository.count();
        
        Map<String, Object> response = Map.of(
                "content", transactions,
                "page", page,
                "size", limit,
                "totalElements", total,
                "totalPages", (int) Math.ceil((double) total / limit),
                "first", page == 1,
                "last", page >= Math.ceil((double) total / limit)
        );
        
        return ApiResponse.Success.of(response);
    }
}
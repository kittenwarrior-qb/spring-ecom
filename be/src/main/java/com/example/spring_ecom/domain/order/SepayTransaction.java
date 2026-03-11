package com.example.spring_ecom.domain.order;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SepayTransaction(
    Long id,
    Integer sepayId,
    JsonNode webhookData,
    String code,
    BigDecimal transferAmount,
    String transferType,
    Boolean processed,
    Long orderId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    // Helper methods to extract data from JSONB
    public String getGateway() {
        return webhookData != null && webhookData.has("gateway") ? 
               webhookData.get("gateway").asText() : null;
    }
    
    public String getTransactionDate() {
        return webhookData != null && webhookData.has("transactionDate") ? 
               webhookData.get("transactionDate").asText() : null;
    }
    
    public String getAccountNumber() {
        return webhookData != null && webhookData.has("accountNumber") ? 
               webhookData.get("accountNumber").asText() : null;
    }
    
    public String getContent() {
        return webhookData != null && webhookData.has("content") ? 
               webhookData.get("content").asText() : null;
    }
    
    public String getReferenceCode() {
        return webhookData != null && webhookData.has("referenceCode") ? 
               webhookData.get("referenceCode").asText() : null;
    }
}
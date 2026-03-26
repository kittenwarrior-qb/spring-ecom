package com.example.spring_ecom.domain.order;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

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
        return Objects.nonNull(webhookData) && webhookData.has("gateway") ? 
               webhookData.get("gateway").asText() : null;
    }
    
    public String getTransactionDate() {
        return Objects.nonNull(webhookData) && webhookData.has("transactionDate") ? 
               webhookData.get("transactionDate").asText() : null;
    }
    
    public String getAccountNumber() {
        return Objects.nonNull(webhookData) && webhookData.has("accountNumber") ? 
               webhookData.get("accountNumber").asText() : null;
    }
    
    public String getContent() {
        return Objects.nonNull(webhookData) && webhookData.has("content") ? 
               webhookData.get("content").asText() : null;
    }
    
    public String getReferenceCode() {
        return Objects.nonNull(webhookData) && webhookData.has("referenceCode") ? 
               webhookData.get("referenceCode").asText() : null;
    }
}
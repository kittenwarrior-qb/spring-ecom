package com.example.spring_ecom.controller.api.payment.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SepayWebhookRequest(
        @JsonProperty("id")
        Integer id,
        
        @JsonProperty("gateway")
        String gateway,
        
        @JsonProperty("transactionDate")
        String transactionDate,
        
        @JsonProperty("accountNumber")
        String accountNumber,
        
        @JsonProperty("code")
        String code,
        
        @JsonProperty("content")
        String content,
        
        @JsonProperty("transferType")
        String transferType,
        
        @JsonProperty("transferAmount")
        Long transferAmount,
        
        @JsonProperty("accumulated")
        Long accumulated,
        
        @JsonProperty("subAccount")
        String subAccount,
        
        @JsonProperty("referenceCode")
        String referenceCode,
        
        @JsonProperty("description")
        String description
) {
}
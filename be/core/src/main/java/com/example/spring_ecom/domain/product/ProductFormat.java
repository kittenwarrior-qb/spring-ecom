package com.example.spring_ecom.domain.product;

import java.util.Objects;

public enum ProductFormat {
    PAPERBACK("Paperback"),
    HARDCOVER("Hardcover"),
    EBOOK("Ebook"),
    AUDIOBOOK("Audiobook");
    
    private final String displayName;
    
    ProductFormat(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static ProductFormat fromString(String value) {
        if (Objects.isNull(value)) {
            return null;
        }
        
        for (ProductFormat format : ProductFormat.values()) {
            if (format.displayName.equalsIgnoreCase(value) || 
                format.name().equalsIgnoreCase(value)) {
                return format;
            }
        }
        
        throw new IllegalArgumentException("Invalid format: " + value + 
            ". Must be one of: Paperback, Hardcover, Ebook, Audiobook");
    }
}

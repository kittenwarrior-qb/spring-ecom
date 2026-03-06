package com.example.spring_ecom.core.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final int code;

    public ResourceNotFoundException(String message) {
        super(message);
        this.code = 404;
    }

    public ResourceNotFoundException(String message, int code) {
        super(message);
        this.code = code;
    }
}

package com.example.spring_ecom.core.exception;

import com.example.spring_ecom.core.response.ValueResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ValueResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ValueResponse<Void> response = ValueResponse.error(ex.getCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ValueResponse<Void>> handleRuntimeException(RuntimeException ex) {
        ValueResponse<Void> response = ValueResponse.error(500, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ValueResponse<Void>> handleException(Exception ex) {
        ValueResponse<Void> response = ValueResponse.error(500, "Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

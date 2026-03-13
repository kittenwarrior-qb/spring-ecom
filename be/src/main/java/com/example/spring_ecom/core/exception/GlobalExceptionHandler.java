package com.example.spring_ecom.core.exception;

import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException ex) {
        log.error("BaseException: {}", ex.getMessage(), ex);
        ApiResponse<Void> response = ApiResponse.Error.of(ex.getResponseCode(), ex.getMessage());
        HttpStatus status = HttpStatus.valueOf(ex.getResponseCode().getCode());
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler({ClientAbortException.class, HttpMessageNotWritableException.class})
    public void handleClientAbortException(Exception ex) {
        // Client đã ngắt kết nối - không cần log error, chỉ log debug
        log.debug("Client disconnected: {}", ex.getMessage());
        // Không return response vì client đã ngắt kết nối
    }

    @ExceptionHandler(IOException.class)
    public void handleIOException(IOException ex) {
        String message = ex.getMessage();
        if (message != null && (message.contains("connection was aborted") || 
                               message.contains("Broken pipe") ||
                               message.contains("Connection reset"))) {
            log.debug("Client connection issue: {}", message);
        } else {
            log.error("IOException: {}", message, ex);
        }
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.warn("NoResourceFoundException: {}", ex.getMessage());
        ApiResponse<Void> response = ApiResponse.Error.of(ResponseCode.ENDPOINT_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("DataIntegrityViolationException: {}", ex.getMessage(), ex);
        
        String message = "Data integrity violation";
        String rootCauseMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
        
        if (rootCauseMessage != null) {
            if (rootCauseMessage.contains("duplicate key value violates unique constraint")) {
                if (rootCauseMessage.contains("products_slug_key")) {
                    message = "Product slug already exists";
                } else if (rootCauseMessage.contains("users_email_key")) {
                    message = "Email already exists";
                } else if (rootCauseMessage.contains("users_username_key")) {
                    message = "Username already exists";
                } else if (rootCauseMessage.contains("categories_slug_key")) {
                    message = "Category slug already exists";
                } else {
                    message = "This value already exists";
                }
            } else if (rootCauseMessage.contains("violates foreign key constraint")) {
                message = "Referenced data does not exist";
            } else if (rootCauseMessage.contains("violates not-null constraint")) {
                message = "Required field is missing";
            }
        }
        
        ApiResponse<Void> response = ApiResponse.Error.of(ResponseCode.BAD_REQUEST, message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        // Kiểm tra nếu là lỗi do client disconnect
        Throwable cause = ex.getCause();
        if (cause instanceof HttpMessageNotWritableException) {
            log.debug("Client disconnected during response writing: {}", ex.getMessage());
            return null; // Không trả response vì client đã ngắt kết nối
        }
        
        log.error("RuntimeException: {}", ex.getMessage(), ex);
        ApiResponse<Void> response = ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        log.error("Exception: {}", ex.getMessage(), ex);
        ApiResponse<Void> response = ApiResponse.Error.of(ResponseCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

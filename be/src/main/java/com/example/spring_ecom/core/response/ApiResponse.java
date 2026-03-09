package com.example.spring_ecom.core.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public static class Success {

        public static <T> ApiResponse<T> of(T data) {
            return ApiResponse.<T>builder()
                    .code(ResponseCode.OK.getCode())
                    .message(ResponseCode.OK.getMessage())
                    .data(data)
                    .build();
        }
        public static ApiResponse<Void> of() {
            return ApiResponse.<Void>builder()
                    .code(ResponseCode.OK.getCode())
                    .message(ResponseCode.OK.getMessage())
                    .data(null)
                    .build();
        }

        public static <T> ApiResponse<T> of(ResponseCode responseCode, T data) {
            return ApiResponse.<T>builder()
                    .code(responseCode.getCode())
                    .message(responseCode.getMessage())
                    .data(data)
                    .build();
        }
        public static ApiResponse<Void> of(ResponseCode responseCode) {
            return ApiResponse.<Void>builder()
                    .code(responseCode.getCode())
                    .message(responseCode.getMessage())
                    .data(null)
                    .build();
        }

        public static <T> ApiResponse<T> of(ResponseCode responseCode, String customMessage, T data) {
            return ApiResponse.<T>builder()
                    .code(responseCode.getCode())
                    .message(customMessage)
                    .data(data)
                    .build();
        }
        public static ApiResponse<Void> of(ResponseCode responseCode, String customMessage) {
            return ApiResponse.<Void>builder()
                    .code(responseCode.getCode())
                    .message(customMessage)
                    .data(null)
                    .build();
        }
    }

    public static class Error {
        public static <T> ApiResponse<T> of(int code, String message) {
            return ApiResponse.<T>builder()
                    .code(code)
                    .message(message)
                    .data(null)
                    .build();
        }
        public static <T> ApiResponse<T> of(ResponseCode responseCode) {
            return ApiResponse.<T>builder()
                    .code(responseCode.getCode())
                    .message(responseCode.getMessage())
                    .data(null)
                    .build();
        }
        public static <T> ApiResponse<T> of(ResponseCode responseCode, String customMessage) {
            return ApiResponse.<T>builder()
                    .code(responseCode.getCode())
                    .message(customMessage)
                    .data(null)
                    .build();
        }
    }
}

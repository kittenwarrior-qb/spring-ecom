package com.example.spring_ecom.core.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValueResponse<T> {
    private int code;
    private String message;
    private T data;
    /**
     * Tạo Response Thành Công có dữ liệu.
     */
    public static <T> ValueResponse<T> success(T data) {
        return ValueResponse.<T>builder()
                .code(200)
                .message("Success")
                .data(data)
                .build();
    }
    /**
     * Tạo Response Thành Công không trả data (Ví dụ: Delete ).
     */
    public static ValueResponse<Void> success() {
        return ValueResponse.<Void>builder()
                .code(200)
                .message("Success")
                .data(null)
                .build();
    }
    /**
     * Tạo Response Thất Bại (Chặn ở Global Exception).
     */
    public static <T> ValueResponse<T> error(int code, String message) {
        return ValueResponse.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .build();
    }
}
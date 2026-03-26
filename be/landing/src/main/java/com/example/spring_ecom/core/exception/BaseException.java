package com.example.spring_ecom.core.exception;

import com.example.spring_ecom.core.response.ResponseCode;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final ResponseCode responseCode;

    public BaseException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
    }

    public BaseException(ResponseCode responseCode, String customMessage) {
        super(customMessage);
        this.responseCode = responseCode;
    }

    public BaseException(ResponseCode responseCode, Throwable cause) {
        super(responseCode.getMessage(), cause);
        this.responseCode = responseCode;
    }
}

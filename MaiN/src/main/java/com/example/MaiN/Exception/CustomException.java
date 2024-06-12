package com.example.MaiN.Exception;

import okhttp3.internal.http2.ErrorCode;

public class CustomException extends RuntimeException {

    private CustomErrorCode customErrorCode;

    public CustomException(String message, CustomErrorCode customErrorCode) {
        super(message);
        this.customErrorCode = customErrorCode;
    }

    public CustomException(CustomErrorCode customErrorCode) {
        super(customErrorCode.getMessage());
        this.customErrorCode = customErrorCode;
    }

    public CustomErrorCode getErrorCode() {
        return this.customErrorCode;
    }
}
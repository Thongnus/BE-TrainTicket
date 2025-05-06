package com.example.betickettrain.exceptions;

public enum ErrorCode {

    PAYMENT_METHOD_NOT_ALLOWED("E0317", "Payment method not allowed"),
    ;


    public final String code;
    public final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}

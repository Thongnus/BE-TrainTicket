package com.example.betickettrain.exceptions;

public enum ErrorCode {

    PAYMENT_METHOD_NOT_ALLOWED("A01", "Payment method not allowed"),
    RESOURCE_NOT_FOUND("A02", "Resource not found"),
    TRAIN_NOT_FOUND("A03", "Train not found"),
    VALIDATION_FAILED("A04", "Validation failed"),
    INTERNAL_ERROR("A05", "Internal server error"),
    ROUTER_NOT_FOUND("A06","Router not found"),
    CARRIAGE_NOT_FOUND("A07","Carriage not found"),
    SEAT_LOCK("SEATLOCK","Seat is locked")
    ;

    public final String code;
    public final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}

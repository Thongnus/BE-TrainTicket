package com.example.betickettrain.exceptions;

public enum ErrorCode {
    USER_NOT_FOUND("U01","User Not Found"),
    USERNAME_ALREADY("U02","Username Already Exist"),
    PAYMENT_METHOD_NOT_ALLOWED("A01", "Payment method not allowed"),
    RESOURCE_NOT_FOUND("A02", "Resource not found"),
    TRAIN_NOT_FOUND("A03", "Train not found"),
    VALIDATION_FAILED("A04", "Validation failed"),
    INTERNAL_ERROR("A05", "Internal server error"),
    ROUTER_NOT_FOUND("A06","Router not found"),
    CARRIAGE_NOT_FOUND("A07","Carriage not found"),
    SEAT_LOCK("SEATLOCK","Seat is locked"),
    DUPLICATE_STATION("A08", "Duplicate entry"),
    REFUND_POLICY("A09", "Refund policy not met"),
    INVALID_DISCOUNT("A10", "Invalid discount code"),;

    public final String code;
    public final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}

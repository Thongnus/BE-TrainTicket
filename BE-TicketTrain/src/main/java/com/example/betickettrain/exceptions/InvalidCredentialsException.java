// InvalidCredentialsException.java
package com.example.betickettrain.exceptions;

public class InvalidCredentialsException extends RuntimeException {
    private final String code;
    
    public InvalidCredentialsException(String message) {
        super(message);
        this.code = "INVALID_CREDENTIALS";
    }
    
    public String getCode() {
        return code;
    }
}
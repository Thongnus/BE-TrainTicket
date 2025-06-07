// AccountDeactivatedException.java
package com.example.betickettrain.exceptions;

public class AccountDeactivatedException extends RuntimeException {
    private final String code;
    
    public AccountDeactivatedException(String message) {
        super(message);
        this.code = "ACCOUNT_DEACTIVATED";
    }
    
    public AccountDeactivatedException(String code, String message) {
        super(message);
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
}
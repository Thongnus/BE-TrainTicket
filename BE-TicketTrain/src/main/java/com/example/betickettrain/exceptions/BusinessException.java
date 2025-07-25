package com.example.betickettrain.exceptions;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{
    private  String errorCode;

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String message) {
        super(message);

    }

}

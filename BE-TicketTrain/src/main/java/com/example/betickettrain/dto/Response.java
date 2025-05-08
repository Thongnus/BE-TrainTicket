package com.example.betickettrain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class Response<T> {
    T data;
    int status = HttpStatus.OK.value();
    String errorCode;
    String errorMessage;
    String message;

    public Response() {}

    public Response(T data) {
        this.data = data;
    }

    public Response(int status, String errorCode, String errorMessage) {
        super();
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.message = errorMessage;
    }
}

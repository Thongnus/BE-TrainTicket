package com.example.betickettrain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;


@AllArgsConstructor
@Getter
@Setter
public class Response<T> {
    private T data;
    private int status = HttpStatus.OK.value();
    private String code = "SUCCESS";
    private String message = "Thành công";
    private long timestamp = System.currentTimeMillis();

    public Response() {}

    public Response(T data) {
        this.data = data;
    }

    // Static factory methods
    public static <T> Response<T> of(T data) {
        return new Response<>(data);
    }

    public static <T> Response<T> of(T data, String message) {
        Response<T> response = new Response<>(data);
        response.setMessage(message);
        return response;
    }

    public static <T> Response<T> error(int status, String code, String message) {
        Response<T> response = new Response<>();
        response.setStatus(status);
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    public  static <T>   Response<T> error(int status, String code, String message, T data) {
        Response<T> response = new Response<>();
        response.setStatus(status);
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }
}

package com.example.betickettrain.exceptions;

import com.example.betickettrain.dto.Response;
import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
@Slf4j
@RestControllerAdvice

public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleInternal(Exception ex) {
        log.error("Exception: - " + ex.getMessage());
        ex.printStackTrace();
        Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(),null, ex.getMessage());
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

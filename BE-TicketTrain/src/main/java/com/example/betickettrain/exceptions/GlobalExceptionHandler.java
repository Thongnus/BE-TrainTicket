package com.example.betickettrain.exceptions;

import com.example.betickettrain.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice

public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response<Void> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return Response.error(
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND",
                ex.getMessage()
        );
    }
//    @ExceptionHandler(ValidationException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public Response<Void> handleValidationException(ValidationException ex) {
//        return Response.error(
//                HttpStatus.BAD_REQUEST.value(),
//                "VALIDATION_ERROR",
//                ex.getMessage()
//        );
//    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response<Void> handleGenericException(Exception ex) {
        log.error("Unhandled exception", ex);
        return Response.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_ERROR",
                ex.getMessage()
        );
    }

    @ExceptionHandler(SeatLockedException.class)
    public ResponseEntity<Response<List<Integer>>> handleSeatLockedException(SeatLockedException ex) {
        log.error("Exception: {} - {}", ex.getCode(), ex.getMessage());
        HttpStatus status = HttpStatus.CONFLICT;

        Response<List<Integer>> response = Response.error(
                status.value(),
                ex.getCode(),
                ex.getMessage(),
                ex.getLockedSeats() // Trả về danh sách ghế bị khóa
        );
        return new ResponseEntity<>(response, status);
    }
}
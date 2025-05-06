package com.example.betickettrain.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    public static class ErrorResponse {
        private String message;
        private String code;
        private int status;
        private String path;
        private LocalDateTime time;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public LocalDateTime getTime() {
            return time;
        }

        public void setTime(LocalDateTime time) {
            this.time = time;
        }
    }

    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ChangeSetPersister.NotFoundException ex, HttpServletRequest request) {
        return buildError(ex.getMessage(), "ERR-404", HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        return buildError(ex.getMessage(), "ERR-400", HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternal(Exception ex, HttpServletRequest request) {
        return buildError("Lỗi hệ thống", "ERR-500", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ErrorResponse> buildError(String message, String code, HttpStatus status, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse();
        response.setMessage(message);
        response.setCode(code);
        response.setStatus(status.value());
        response.setPath(request.getRequestURI());
        response.setTime(LocalDateTime.now());
        return new ResponseEntity<>(response, status);
    }
}

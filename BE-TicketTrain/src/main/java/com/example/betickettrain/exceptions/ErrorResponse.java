package com.example.betickettrain.exceptions;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String code;          // VD: ERR-404
    private String message;       // Nội dung lỗi
    private int status;           // HTTP status code
    private String path;          // API path gây lỗi
    private LocalDateTime time;   // Thời điểm xảy ra lỗi

    // constructor, getters, setters
}

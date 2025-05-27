package com.example.betickettrain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VnPayCallbackResponse {
    private boolean success;
    private String message;

    public VnPayCallbackResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // getters, setters
}

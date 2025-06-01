package com.example.betickettrain.exceptions;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
@Getter
@Setter
public class SeatLockedException extends RuntimeException {
    private  String code;
    private String message;
    private List<Integer> lockedSeats;
    public SeatLockedException(ErrorCode error) {
        this.code = error.code;
        this.message = error.message;
        this.lockedSeats = null;
    }

    public SeatLockedException(ErrorCode error, String message) {
        this.code = error.code;
        if (StringUtils.isNotBlank(message)) {
            this.message = message;
        } else {
            this.message = error.message;
        }
    }
    public SeatLockedException(ErrorCode error, String message, List<Integer> lockedSeats) {
        this.code = error.code;
        this.message = StringUtils.isNotBlank(message) ? message : error.message;
        this.lockedSeats = lockedSeats;
    }
    // getters...
}
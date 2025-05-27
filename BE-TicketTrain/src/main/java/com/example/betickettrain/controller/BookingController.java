package com.example.betickettrain.controller;


import com.example.betickettrain.dto.BookingCheckoutRequest;
import com.example.betickettrain.dto.BookingLockRequest;
import com.example.betickettrain.entity.User;
import com.example.betickettrain.service.BookingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/bookings")
@AllArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/lock-seats")
    public ResponseEntity<?> lockSeats(@RequestBody BookingLockRequest request) {
        bookingService.lockSeats(request);
        return ResponseEntity.ok(Map.of("message", "Seats locked successfully"));
    }
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody BookingCheckoutRequest request,@AuthenticationPrincipal User user) {

        String paymentUrl = bookingService.initiateCheckout(request,user);
        return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
    }

}

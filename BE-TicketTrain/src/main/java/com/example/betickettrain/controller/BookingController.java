package com.example.betickettrain.controller;


import com.example.betickettrain.dto.BookingCheckoutRequest;
import com.example.betickettrain.dto.BookingDto;
import com.example.betickettrain.dto.BookingHistoryDTO;
import com.example.betickettrain.dto.BookingLockRequest;
import com.example.betickettrain.entity.User;
import com.example.betickettrain.service.BookingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/bookings")
@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/lock-seats")
    public ResponseEntity<?> lockSeats(@RequestBody BookingLockRequest request) {
        bookingService.lockSeats(request);
        return ResponseEntity.ok(Map.of("message", "Seats locked successfully"));
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody BookingCheckoutRequest request, @AuthenticationPrincipal User user) {

        String paymentUrl = bookingService.initiateCheckout(request, user);
        return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
    }

    @GetMapping
    public ResponseEntity<Page<BookingDto>> searchBookings(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "all") String bookingStatus,
            @RequestParam(required = false, defaultValue = "all") String paymentStatus,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<BookingDto> result = bookingService.findBookings(search, bookingStatus, paymentStatus, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history")
    public ResponseEntity<List<BookingHistoryDTO>> getBookingHistory(@AuthenticationPrincipal User user) {
        List<BookingHistoryDTO> bookings = bookingService.getBookingHistorybyUser(user.getUserId());
        return ResponseEntity.ok(bookings);


    }
}

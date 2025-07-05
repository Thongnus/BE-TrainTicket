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
import org.springframework.security.access.prepost.PreAuthorize;
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
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<BookingDto> result = bookingService.findBookings(search, bookingStatus, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history")
    public ResponseEntity<Page<BookingHistoryDTO>> getBookingHistory(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<BookingHistoryDTO> bookings = bookingService.getBookingHistorybyUser(user.getUserId(), pageable);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/checkin")
    @PreAuthorize("hasRole('ADMIN')") //
    //tam thoi chua build 1 app rieng gianh cho viec check-in nên tạm thòi quét thì tự động xác nhận check-in
    public ResponseEntity<?> checkInBooking(@RequestParam("code") String bookingCode) {
        try {
            BookingDto booking = bookingService.findBookingByBookingCode(bookingCode);
            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Không tìm thấy đơn đặt vé"));
            }

            // (Tùy chọn) Xác nhận trạng thái vé và cập nhật
            bookingService.markTicketsCheckedIn(booking.getBookingId());

            return ResponseEntity.ok(Map.of(
                    "message", "Check-in thành công",
                    "bookingCode", booking.getBookingCode(),
                    "bookingDate", booking.getBookingDate(),
                    "totalAmount", booking.getTotalAmount()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Đã xảy ra lỗi khi check-in"));
        }
    }
    @PostMapping("/cancel/{bookingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cancelBooking(@PathVariable() Integer bookingId) {
        boolean isCancelled = bookingService.cancelBookingByAdmin(bookingId);
        if (isCancelled) {
            return ResponseEntity.ok(Map.of("message", "Đơn đặt vé đã được hủy thành công"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Không tìm thấy đơn đặt vé hoặc không thể hủy"));
        }
    }
}

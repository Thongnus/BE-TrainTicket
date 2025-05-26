package com.example.betickettrain.controller;

import com.example.betickettrain.service.BookingService;
import com.example.betickettrain.service.ServiceImpl.VnpayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentCallbackController {
    private final VnpayService vnpayService;
    private final BookingService bookingService;

    @GetMapping("/vnpay/callback")
    public ResponseEntity<String> vnPayCallback(HttpServletRequest request) {
        if (!vnpayService.validateSignature(request)) {
            return ResponseEntity.status(403).body("Invalid signature");
        }

        String bookingCode = request.getParameter("vnp_TxnRef");
        String responseCode = request.getParameter("vnp_ResponseCode");

        boolean success = bookingService.handleVnPayCallback(bookingCode, responseCode);
        return ResponseEntity.ok(success ? "Thanh toán thành công" : "Thanh toán thất bại");
    }
}

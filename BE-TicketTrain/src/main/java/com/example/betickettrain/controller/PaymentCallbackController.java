package com.example.betickettrain.controller;

import com.example.betickettrain.dto.VnPayCallbackResponse;
import com.example.betickettrain.entity.User;
import com.example.betickettrain.service.BookingService;
import com.example.betickettrain.service.ServiceImpl.VnpayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentCallbackController {
    private final VnpayService vnpayService;
    private final BookingService bookingService;

    @GetMapping("/vnpay/callback")
    public ResponseEntity<?> vnPayCallback(HttpServletRequest request) {
        if (!vnpayService.validateSignature(request)) {
            // Redirect về FE với lỗi
            return ResponseEntity.status(302)
                    .header("Location", "https://your-frontend.com/payment-return?success=false&message=Invalid signature")
                    .build();
        }

        String bookingCode = request.getParameter("vnp_TxnRef");
        String responseCode = request.getParameter("vnp_ResponseCode");

        boolean success = bookingService.handleVnPayCallback(bookingCode, responseCode);
        String message = success ? "Thanh toán thành công" : "Thanh toán thất bại";

        String redirectUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/payment-return")
                .queryParam("success", success)
                .queryParam("message", URLEncoder.encode(message, StandardCharsets.UTF_8))
                .build().toUriString();
//302 Redirect
        return ResponseEntity.status(302)
                .header("Location", redirectUrl)
                .build();
    }

}

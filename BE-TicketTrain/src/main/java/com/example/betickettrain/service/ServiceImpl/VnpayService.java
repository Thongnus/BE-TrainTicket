package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.configuration.VnPayProperties;
import com.example.betickettrain.entity.Booking;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VnpayService {
    private final VnPayProperties vnpayProps;

    public String generatePaymentUrl(Booking booking) {
        String vnp_TxnRef = booking.getBookingCode();
        String vnp_OrderInfo = "Thanh toan ve tau " + booking.getBookingCode();
        String vnp_Amount = String.valueOf((long)(booking.getTotalAmount() * 100));

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnpayProps.getTmnCode());
        vnp_Params.put("vnp_Amount", vnp_Amount);
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnpayProps.getReturnUrl());
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");
        vnp_Params.put("vnp_CreateDate", DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(booking.getCreatedAt()));

        return buildSecureUrl(vnp_Params);
    }

    public boolean validateSignature(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            if (!"vnp_SecureHash".equals(entry.getKey()) && !"vnp_SecureHashType".equals(entry.getKey())) {
                fields.put(entry.getKey(), entry.getValue()[0]);
            }
        }

        List<String> sortedKeys = new ArrayList<>(fields.keySet());
        Collections.sort(sortedKeys);

        StringBuilder hashData = new StringBuilder();
        for (String key : sortedKeys) {
            hashData.append(key).append('=').append(fields.get(key));
            if (!key.equals(sortedKeys.get(sortedKeys.size() - 1))) {
                hashData.append('&');
            }
        }

        String secureHash = hmacSHA512(vnpayProps.getHashSecret(), hashData.toString());
        String receivedHash = request.getParameter("vnp_SecureHash");
        return secureHash.equalsIgnoreCase(receivedHash);
    }

    private String buildSecureUrl(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String fieldName : fieldNames) {
            String value = params.get(fieldName);
            if ((value != null) && (!value.isEmpty())) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII)).append('&');
                query.append(fieldName).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII)).append('&');
            }
        }

        hashData.setLength(hashData.length() - 1);
        query.setLength(query.length() - 1);

        String secureHash = hmacSHA512(vnpayProps.getHashSecret(), hashData.toString());
        return vnpayProps.getUrl() + "?" + query + "&vnp_SecureHash=" + secureHash;
    }

    private String hmacSHA512(String key, String data) {
        try {
            javax.crypto.Mac hmac512 = javax.crypto.Mac.getInstance("HmacSHA512");
            javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(key.getBytes(), "HmacSHA512");
            hmac512.init(secretKeySpec);
            byte[] bytes = hmac512.doFinal(data.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC SHA512", e);
        }
    }
}

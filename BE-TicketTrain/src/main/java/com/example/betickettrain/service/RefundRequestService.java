package com.example.betickettrain.service;

import com.example.betickettrain.dto.RefundRequestDto;
import com.example.betickettrain.dto.RefundStatisticsDto;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface RefundRequestService {
    @Transactional
    void requestRefundBooking(Integer bookingId);

    Page<RefundRequestDto> getRefundRequests(String search,String status, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
    RefundRequestDto getRefundRequestById(Long refundRequestId);

    void approveRefundRequest(Long refundRequestId, String adminNote);

    void rejectRefundRequest(Long refundRequestId, String reason);

    RefundStatisticsDto getRefundStatistics();
}

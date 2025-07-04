package com.example.betickettrain.controller;

import com.example.betickettrain.dto.RefundRequestDto;
import com.example.betickettrain.dto.RefundStatisticsDto;
import com.example.betickettrain.dto.Response;
import com.example.betickettrain.entity.Booking;
import com.example.betickettrain.service.RefundPolicyService;
import com.example.betickettrain.service.RefundRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundRequestService requestRefundBooking;

    @PostMapping("/booking/{bookingId}")
    public ResponseEntity<?> requestRefundBooking(@PathVariable Integer bookingId) {
        requestRefundBooking.requestRefundBooking(bookingId);
        return ResponseEntity.ok("Yêu cầu hoàn tiền cho booking #" + bookingId + " đã được gửi.");
    }
    @GetMapping("/requests")
    public Response<?> getRefundRequests(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String status, // ✅ thêm dòng này
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());

        Page<RefundRequestDto> pageResult = requestRefundBooking.getRefundRequests(search, status, fromDate, toDate, pageable);

        return Response.of(pageResult);
    }

    @GetMapping("/{refundRequestId}")
    public Response<?> getRefundRequestById(@PathVariable Long refundRequestId) {
        RefundRequestDto dto = requestRefundBooking.getRefundRequestById(refundRequestId);
        return Response.of(dto);
    }
    @PutMapping("/{refundRequestId}/approve")
    public ResponseEntity<?> approveRefundRequest(@PathVariable Long refundRequestId,
                                                  @RequestParam(required = false) String adminNote) {
        requestRefundBooking.approveRefundRequest(refundRequestId, adminNote);
        return ResponseEntity.ok("Yêu cầu hoàn tiền #" + refundRequestId + " đã được duyệt.");
    }

    @PutMapping("/{refundRequestId}/reject")
    public ResponseEntity<?> rejectRefundRequest(@PathVariable Long refundRequestId,
                                                 @RequestParam String reason) {
        requestRefundBooking.rejectRefundRequest(refundRequestId, reason);
        return ResponseEntity.ok("Yêu cầu hoàn tiền #" + refundRequestId + " đã bị từ chối.");
    }
    @GetMapping("/statistics")
    public Response<RefundStatisticsDto> getRefundStatistics() {
        RefundStatisticsDto stats = requestRefundBooking.getRefundStatistics();
        return Response.of(stats);
    }


}

package com.example.betickettrain.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class RefundStatisticsDto {
    private Long totalRequests;
    private BigDecimal totalRefundAmount;
    private Long approvedCount;
    private Long rejectedCount;
    private Long pendingCount;

    public RefundStatisticsDto(Long totalRequests, BigDecimal totalRefundAmount,
                               Long approvedCount, Long rejectedCount, Long pendingCount) {
        this.totalRequests = totalRequests;
        this.totalRefundAmount = totalRefundAmount;
        this.approvedCount = approvedCount;
        this.rejectedCount = rejectedCount;
        this.pendingCount = pendingCount;
    }


}

package com.example.betickettrain.dto;

import java.math.BigDecimal;

public interface RefundStatisticsProjection {
    Long getTotalRequests();
    BigDecimal getTotalRefundAmount();
    Long getApprovedCount();
    Long getRejectedCount();
    Long getPendingCount();
}

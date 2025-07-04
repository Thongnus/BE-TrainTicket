package com.example.betickettrain.repository;

import com.example.betickettrain.dto.RefundStatisticsDto;
import com.example.betickettrain.dto.RefundStatisticsProjection;
import com.example.betickettrain.entity.RefundRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundRequestRepository extends JpaRepository<RefundRequest, Long>, JpaSpecificationExecutor<RefundRequest> {
    @Query("""
    SELECT 
        COUNT(r) AS totalRequests,
        COALESCE(SUM(r.refundAmount), 0) AS totalRefundAmount,
        SUM(CASE WHEN r.status = 'approved' THEN 1 ELSE 0 END) AS approvedCount,
        SUM(CASE WHEN r.status = 'rejected' THEN 1 ELSE 0 END) AS rejectedCount,
        SUM(CASE WHEN r.status = 'pending' THEN 1 ELSE 0 END) AS pendingCount
    FROM RefundRequest r
""")
    RefundStatisticsProjection getRefundStatisticsProjection();


}
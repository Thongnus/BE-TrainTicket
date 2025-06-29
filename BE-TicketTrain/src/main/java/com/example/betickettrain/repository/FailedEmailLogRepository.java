package com.example.betickettrain.repository;

import com.example.betickettrain.entity.FailedEmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FailedEmailLogRepository extends JpaRepository<FailedEmailLog, Long> {
    List<FailedEmailLog> findByResolvedFalseAndRetryCountLessThan(int maxRetry);
}
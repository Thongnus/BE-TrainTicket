package com.example.betickettrain.repository;

import com.example.betickettrain.entity.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemLogRepository extends JpaRepository<SystemLog, Integer> {
}
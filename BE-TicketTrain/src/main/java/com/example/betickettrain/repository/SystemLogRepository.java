package com.example.betickettrain.repository;

import com.example.betickettrain.entity.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SystemLogRepository extends JpaRepository<SystemLog, Integer> {

    List<SystemLog> findTop10ByOrderByLogTimeDesc();
}
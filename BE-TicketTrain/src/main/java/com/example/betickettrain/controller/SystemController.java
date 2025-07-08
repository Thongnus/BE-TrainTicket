package com.example.betickettrain.controller;

import com.example.betickettrain.service.SystemLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SystemController {

    private final SystemLogService systemLogService;

   @GetMapping("/logs")
    public ResponseEntity<?> getSystemLogs() {
        log.info("Fetching system logs");
        return ResponseEntity.ok(systemLogService.FindAllLogs());
    }

//    @GetMapping("/status")
//    public String getSystemStatus() {
//        log.info("Fetching system status");
//        return systemLogService.getSystemStatus();
//    }
//
//    @GetMapping("/health")
//    public String getHealthCheck() {
//        log.info("Performing health check");
//        return systemLogService.getHealthCheck();
//    }




}

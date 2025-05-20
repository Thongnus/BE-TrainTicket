package com.example.betickettrain.schedule;

import com.example.betickettrain.service.GenericCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class CacheCleanupConfig {

    private final GenericCacheService cacheService;
    
    // Chạy task làm sạch cache mỗi 5 phút
    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredCacheEntries() {
        cacheService.clearAllCaches();
    }
}
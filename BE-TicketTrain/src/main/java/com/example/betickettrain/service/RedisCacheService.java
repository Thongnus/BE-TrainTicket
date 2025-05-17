package com.example.betickettrain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheService {

    private static final String KEY_PREFIX = "redisCache:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Lưu đối tượng vào Redis
    public void cacheData(String key, Object data) {
        String cacheKey = KEY_PREFIX + key;
        redisTemplate.opsForValue().set(cacheKey, data);
        // Tùy chọn: cài đặt thời gian hết hạn
        redisTemplate.expire(cacheKey, 1, TimeUnit.HOURS);
    }

    // Lấy đối tượng từ Redis
    public Object getCachedData(String key) {
        return redisTemplate.opsForValue().get(KEY_PREFIX + key);
    }

    // Xóa đối tượng khỏi Redis
    public void deleteCachedData(String key) {
        redisTemplate.delete(KEY_PREFIX + key);
    }

    // Xóa nhiều keys bằng pattern
    public void deleteByPattern(String pattern) {
        String cachePattern = KEY_PREFIX + pattern + "*";
        redisTemplate.keys(cachePattern).forEach(key -> {
            redisTemplate.delete(key);
        });
    }
}
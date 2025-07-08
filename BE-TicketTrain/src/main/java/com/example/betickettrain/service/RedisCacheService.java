package com.example.betickettrain.service;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class RedisCacheService {

    private static final String KEY_PREFIX = "redisCache:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void cacheData(String key, Object data) {
        redisTemplate.opsForValue().set(KEY_PREFIX + key, data);
        redisTemplate.expire(KEY_PREFIX + key, 1, TimeUnit.HOURS);
    }

    public Object getCachedData(String key) {
        return redisTemplate.opsForValue().get(KEY_PREFIX + key);
    }

    // ✅ MỚI: Lấy dữ liệu và convert sang kiểu cụ thể
    public <T> T getCachedData(String key, Class<T> clazz) {
        Object value = getCachedData(key);
        if (value == null) return null;
        return objectMapper.convertValue(value, clazz);
    }

    public void deleteCachedData(String key) {
        redisTemplate.delete(KEY_PREFIX + key);
    }

    public void deleteByPattern(String pattern) {
        redisTemplate.keys(KEY_PREFIX + pattern + "*").forEach(redisTemplate::delete);
    }
    public <T> T getCachedData(String key, TypeReference<T> typeRef) {
        Object value = getCachedData(key);  // giá trị ban đầu từ Redis
        if (value == null) return null;

        // Nếu Redis trả ra JSON string thì deserialize luôn
        if (value instanceof String) {
            try {
                return objectMapper.readValue((String) value, typeRef);
            } catch (Exception e) {
                throw new RuntimeException("❌ Lỗi khi đọc dữ liệu từ Redis (typeRef): " + e.getMessage(), e);
            }
        }

        // Nếu không phải String thì convert trực tiếp
        return objectMapper.convertValue(value, typeRef);
    }
}

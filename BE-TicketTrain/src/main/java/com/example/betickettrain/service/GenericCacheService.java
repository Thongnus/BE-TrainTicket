package com.example.betickettrain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenericCacheService {

    private final RedisCacheService redisCacheService;

    // Local cache
    private final Map<String, Map<Object, Object>> caches = new ConcurrentHashMap<>();

    // Khởi tạo một số cache mặc định (nếu cần)
    private void initializeDefaultCaches() {
        String[] defaultCaches = {"general", "user", "newfeed", "ticket", "train", "station"};
        for (String cacheName : defaultCaches) {
            caches.putIfAbsent(cacheName, new ConcurrentHashMap<>());
        }
    }

    public <K, V> V get(String cacheName, K key, Class<V> clazz) {
        Map<Object, Object> cache = caches.computeIfAbsent(cacheName, k -> new ConcurrentHashMap<>());

        // 1. Kiểm tra local cache
        if (cache != null && cache.containsKey(key)) {
            log.info("🔄 Lấy từ LOCAL cache [{}]: key = {}", cacheName, key);
            return clazz.cast(cache.get(key));
        }

        // 2. Thử lấy từ Redis
        String redisKey = buildRedisKey(cacheName, key);
        V value = redisCacheService.getCachedData(redisKey, clazz);

        if (value != null) {
            if (cache != null) {
                cache.put(key, value); // Ghi lại local
            }
            log.info("☁️  Lấy từ REDIS cache [{}]: key = {}", cacheName, key);
            return value;
        }

        // 3. Không có trong cache
        log.info("❌ Không có trong LOCAL & REDIS cache [{}]: key = {}", cacheName, key);
        return null;
    }


    public void createCache(String cacheName) {
        caches.putIfAbsent(cacheName, new ConcurrentHashMap<>());
    }

    public <K, V> void put(String cacheName, K key, V value) {
        Map<Object, Object> cache = caches.computeIfAbsent(cacheName, k -> new ConcurrentHashMap<>());
        cache.put(key, value);
        String redisKey = buildRedisKey(cacheName, key);
        redisCacheService.cacheData(redisKey, value);
    }

    public <K, V> V get(String cacheName, K key) {
        Map<Object, Object> cache = caches.computeIfAbsent(cacheName, k -> new ConcurrentHashMap<>());
        if (cache != null && cache.containsKey(key)) {
            log.info("🔄 Lấy từ LOCAL cache [{}]: key = {}", cacheName, key);
            return (V) cache.get(key);
        }

        String redisKey = buildRedisKey(cacheName, key);
        Object value = redisCacheService.getCachedData(redisKey);
        if (value != null) {
            if (cache != null) {
                cache.put(key, value);
            }
            log.info("☁️  Lấy từ REDIS cache [{}]: key = {}", cacheName, key);
            return (V) value;
        }

        log.info("❌ Không có trong cache [{}]: key = {}", cacheName, key);
        return null;
    }

    public <K> boolean contains(String cacheName, K key) {
        Map<Object, Object> cache = caches.get(cacheName);
        if (cache != null && cache.containsKey(key)) {
            return true;
        }

        String redisKey = buildRedisKey(cacheName, key);
        return redisCacheService.getCachedData(redisKey) != null;
    }

    public <K> void remove(String cacheName, K key) {
        Map<Object, Object> cache = caches.get(cacheName);
        if (cache != null) {
            cache.remove(key);
        }

        String redisKey = buildRedisKey(cacheName, key);
        redisCacheService.deleteCachedData(redisKey);
    }

    public void clearCache(String cacheName) {
        Map<Object, Object> cache = caches.get(cacheName);
        if (cache != null) {
            cache.clear();
        }

        redisCacheService.deleteByPattern(cacheName + ":");
    }

    public void clearAllCaches() {
        caches.values().forEach(Map::clear);

        for (String cacheName : caches.keySet()) {
            redisCacheService.deleteByPattern(cacheName + ":");
        }
    }

    private <K> String buildRedisKey(String cacheName, K key) {
        return cacheName + ":" + key.toString();
    }

    public int getCacheSize(String cacheName) {
        Map<Object, Object> cache = caches.get(cacheName);
        return cache != null ? cache.size() : 0;
    }

    public boolean cacheExists(String cacheName) {
        return caches.containsKey(cacheName);
    }

    @SuppressWarnings("unchecked")
    public <K> Iterable<K> getKeys(String cacheName) {
        Map<Object, Object> cache = caches.get(cacheName);
        return cache != null ? (Iterable<K>) cache.keySet() : null;
    }
}

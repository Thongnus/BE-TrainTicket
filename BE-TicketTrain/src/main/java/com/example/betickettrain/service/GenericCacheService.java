package com.example.betickettrain.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GenericCacheService {

    // Map of cache instances by name
    private final Map<String, Map<Object, Object>> caches = new HashMap<>();
    
    // Khởi tạo các cache thông dụng
    public GenericCacheService() {
        // Khởi tạo các cache mặc định
        caches.put("general", new ConcurrentHashMap<>());
        caches.put("user", new ConcurrentHashMap<>());
        caches.put("newfeed", new ConcurrentHashMap<>());
        caches.put("ticket", new ConcurrentHashMap<>());
        caches.put("train", new ConcurrentHashMap<>());
    }
    
    // Tạo cache mới theo tên
    public void createCache(String cacheName) {
        if (!caches.containsKey(cacheName)) {
            caches.put(cacheName, new ConcurrentHashMap<>());
        }
    }
    
    // Lưu giá trị vào cache
    @SuppressWarnings("unchecked")
    public <K, V> void put(String cacheName, K key, V value) {
        Map<Object, Object> cache = caches.computeIfAbsent(cacheName, k -> new ConcurrentHashMap<>());
        cache.put(key, value);
    }
    
    // Lấy giá trị từ cache
    @SuppressWarnings("unchecked")
    public <K, V> V get(String cacheName, K key) {
        Map<Object, Object> cache = caches.get(cacheName);
        if (cache != null) {
            return (V) cache.get(key);
        }
        return null;
    }
    
    // Kiểm tra xem key có tồn tại trong cache không
    public <K> boolean contains(String cacheName, K key) {
        Map<Object, Object> cache = caches.get(cacheName);
        return cache != null && cache.containsKey(key);
    }
    
    // Xóa một entry khỏi cache
    public <K> void remove(String cacheName, K key) {
        Map<Object, Object> cache = caches.get(cacheName);
        if (cache != null) {
            cache.remove(key);
        }
    }
    
    // Xóa toàn bộ cache theo tên
    public void clearCache(String cacheName) {
        Map<Object, Object> cache = caches.get(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }
    
    // Xóa tất cả các cache
    public void clearAllCaches() {
        caches.values().forEach(Map::clear);
    }
    
    // Lấy kích thước của cache
    public int getCacheSize(String cacheName) {
        Map<Object, Object> cache = caches.get(cacheName);
        return cache != null ? cache.size() : 0;
    }
    
    // Kiểm tra xem cache có tồn tại không
    public boolean cacheExists(String cacheName) {
        return caches.containsKey(cacheName);
    }
    
    // Lấy tất cả keys trong một cache
    @SuppressWarnings("unchecked")
    public <K> Iterable<K> getKeys(String cacheName) {
        Map<Object, Object> cache = caches.get(cacheName);
        if (cache != null) {
            return (Iterable<K>) cache.keySet();
        }
        return null;
    }
}
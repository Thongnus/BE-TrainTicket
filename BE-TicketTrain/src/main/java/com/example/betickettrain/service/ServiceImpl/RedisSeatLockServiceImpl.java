package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.service.RedisSeatLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisSeatLockServiceImpl implements RedisSeatLockService {

    private final RedisTemplate<String, String> redisTemplate;

    private String buildKey(Integer tripId, Integer seatId) {
        return "lock:trip:" + tripId + ":seat:" + seatId;
    }

    @Override
    public boolean tryLockSeat(Integer tripId, Integer seatId, Duration ttl) {
        String key = buildKey(tripId, seatId);
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "locked", ttl);
        return Boolean.TRUE.equals(success);
    }


    @Override
    public Set<Integer> getLockedSeats(Integer tripId) {
        Set<String> keys = redisTemplate.keys("lock:trip:" + tripId + ":seat:*");
        return keys.stream()
                .map(k -> k.substring(k.lastIndexOf(":") + 1))
                .map(Integer::valueOf)
                .collect(Collectors.toSet());
    }

    @Override
    public void unlockSeat(Integer tripId, Integer seatId) {
        redisTemplate.delete(buildKey(tripId, seatId));
    }

    @Override
    public void extendSeatLock(Integer tripId, Integer seatId, Duration ttl) {
        redisTemplate.expire(buildKey(tripId, seatId), ttl);
    }
}
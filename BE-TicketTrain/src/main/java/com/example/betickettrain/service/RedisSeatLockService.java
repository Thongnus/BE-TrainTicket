package com.example.betickettrain.service;

import java.time.Duration;
import java.util.Set;

public interface RedisSeatLockService {
    boolean tryLockSeat(Integer tripId, Integer seatId, Duration ttl);
    Set<Integer> getLockedSeats(Integer tripId);
    void unlockSeat(Integer tripId, Integer seatId);
    void extendSeatLock(Integer tripId, Integer seatId, Duration ttl);

}
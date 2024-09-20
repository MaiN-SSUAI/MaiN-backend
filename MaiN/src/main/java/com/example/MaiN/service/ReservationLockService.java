package com.example.MaiN.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ReservationLockService {

    private static final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();
    private static final String GLOBAL_RESERVATION_LOCK = "GLOBAL_RESERVATION_LOCK";

    public static <T> T executeWithLock(LocalDateTime startTime, LocalDateTime endTime, LockCallback<T> callback) throws Exception {
        String lockKey = startTime + ":" + endTime;
        Object lock = locks.computeIfAbsent(GLOBAL_RESERVATION_LOCK, k -> new Object());

        synchronized (lock) {
            try {
                return callback.execute();
            } finally {
                locks.remove(lockKey);
            }
        }
    }

    @FunctionalInterface
    public interface LockCallback<T> {
        T execute() throws Exception;
    }
}
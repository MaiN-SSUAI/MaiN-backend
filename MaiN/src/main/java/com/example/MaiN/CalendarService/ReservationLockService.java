package com.example.MaiN.CalendarService;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class ReservationLockService {

    private static final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

    public static <T> T executeWithLock(String startTime, String endTime, LockCallback<T> callback) throws Exception {
        String lockKey = startTime + ":" + endTime;
        Object lock = locks.computeIfAbsent(lockKey, k -> new Object());

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

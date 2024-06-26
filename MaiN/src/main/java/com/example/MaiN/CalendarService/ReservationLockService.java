package com.example.MaiN.CalendarService;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ReservationLockService {

    private static final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

    public static <T> T executeWithLock(String startTime, String endTime, LockCallback<T> callback) throws Exception {
        String lockKey = createLockKey(startTime, endTime);
        Object lock = locks.computeIfAbsent(lockKey, k -> new Object());

        synchronized (lock) {
            try {
                return callback.execute();
            } finally {
                locks.remove(lockKey);
            }
        }
    }

    private static String createLockKey(String startTime, String endTime) {
        ZonedDateTime start = ZonedDateTime.parse(startTime);
        ZonedDateTime end = ZonedDateTime.parse(endTime);

        StringBuilder keyBuilder = new StringBuilder();
        ZonedDateTime current = start;
        while (current.isBefore(end)) {
            keyBuilder.append(current.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))).append(":");
            current = current.plusMinutes(10);
        }
        return keyBuilder.toString();
    }

    @FunctionalInterface
    public interface LockCallback<T> {
        T execute() throws Exception;
    }
}
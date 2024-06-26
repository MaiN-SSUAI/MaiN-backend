package com.example.MaiN.CalendarService;

import org.springframework.stereotype.Service;


@Service
public class ReservationLockService {

    private static final Object lock = new Object();

    public static <T> T executeWithLock(String startTime, String endTime, LockCallback<T> callback) throws Exception {
        String lockKey = startTime + ":" + endTime;
        synchronized (lock) {
            return callback.execute();
        }
    }

    @FunctionalInterface
    public interface LockCallback<T> {
        T execute() throws Exception;
    }
}

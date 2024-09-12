package com.example.MaiN.service;


import com.example.MaiN.dto.PushMessage;
import com.example.MaiN.entity.Reserv;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class NotificationScheduler {

    private final ReservationService reservationService;

    public NotificationScheduler(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Scheduled(cron = "0 */1 * * * ?") // 1분마다 체크하는 스케줄러
    public void pushReservationAlarm() {
        List<Reserv> reservations1 = reservationService.getReservationsStartingIn30Minutes();
        List<Reserv> reservations2 = reservationService.getReservationEndingIn5Minutes();
        for (Reserv reservation : reservations1) {
            try {
                String startTime = reservation.getStartTime();
                String endTime = reservation.getEndTime();
                reservationService.sendNotificationToUsers(
                        reservation.getStudentIds(),
                        PushMessage.MIN_LEFT, startTime, endTime);
            } catch (IOException e) {
                log.error("예약 알림 전송 실패", e);
            }
        }
        for (Reserv reservation : reservations2) {
            try {
                reservationService.sendNotificationToUsers(
                        reservation.getStudentIds(),
                        PushMessage.MIN_LEFT_ENDING);
            } catch (IOException e) {
                log.error("예약 알림 전송 실패", e);
            }
        }
    }
}

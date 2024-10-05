package com.example.MaiN.service;

import com.example.MaiN.dto.PushMessage;
import com.example.MaiN.entity.Reserv;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
@Component
@Slf4j

public class NotificationScheduler {

    private final ReservationService reservationService;

    public NotificationScheduler(ReservationService reservationService) {
        this.reservationService = reservationService;
    }
    @Scheduled(cron = "0 */1 * * * ?") // 1분마다 스케줄러 작동
    @Transactional
    public void pushReservationAlarm() {
        // 30분 전 예약 알림
        List<Reserv> reservationsStartingSoon = reservationService.getReservationsStartingIn30Minutes();
        log.info("예약 시작 30분 전 알림 대상 예약 목록: {}", reservationsStartingSoon);

        for (Reserv reservation : reservationsStartingSoon) {
            try {
                String startTime = String.valueOf(reservation.getStartTime());
                String endTime = String.valueOf(reservation.getEndTime());
                log.info("예약 시작 30분 전 예약입니다.  예약: ID={}, 시작 시간={}, 종료 시간={}",
                        reservation.getId(), startTime, endTime);

                reservationService.sendNotificationToUsers(
                        reservation.getStudentIds(),
                        PushMessage.MIN_LEFT, startTime, endTime);
            } catch (IOException e) {
                log.error("예약 알림 전송 실패", e);
            }
        }

        // 5분 전 예약 알림
        List<Reserv> reservationsEndingSoon = reservationService.getReservationsEndingIn5Minutes();
        log.info("예약 종료 5분 전 알림 대상 예약 목록: {}", reservationsEndingSoon);

        for (Reserv reservation : reservationsEndingSoon) {
            try {
                String startTime = String.valueOf(reservation.getStartTime());
                String endTime = String.valueOf(reservation.getEndTime());
                log.info("예약 종료 5분 전 예약입니다.  예약: ID={}, 시작 시간={}, 종료 시간={}",
                        reservation.getId(), startTime, endTime);

                reservationService.sendNotificationToUsers(
                        reservation.getStudentIds(),
                        PushMessage.MIN_LEFT_ENDING);
            } catch (IOException e) {
                log.error("예약 알림 전송 실패", e);
            }
        }
    }
}
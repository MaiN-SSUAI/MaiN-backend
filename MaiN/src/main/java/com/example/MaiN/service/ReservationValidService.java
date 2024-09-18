package com.example.MaiN.service;

import com.example.MaiN.Exception.CustomErrorCode;
import com.example.MaiN.Exception.CustomException;
//import com.example.MaiN.entity.Event;
import com.example.MaiN.dto.DayReservationResponse;
import com.example.MaiN.dto.SingleReservationDto;
import com.example.MaiN.entity.Reserv;
import com.example.MaiN.entity.ReservAssign;
import com.example.MaiN.repository.ReservAssignRepository;
import com.example.MaiN.repository.ReservRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.DateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReservationValidService {

    private final ReservRepository reservRepository;
    private final ReservAssignRepository reservAssignRepository;
    private final CalendarService calendarService;

    //예약 제한 사항 (예약 시간 관련) 체크
    public void checkReservation(String startDateTimeStr, String endDateTimeStr, List<String> studentIds) throws Exception {
        DateTime startDateTime = new DateTime(startDateTimeStr);
        DateTime endDateTime = new DateTime(endDateTimeStr);
        LocalDate startDate = LocalDate.parse(startDateTimeStr.split("T")[0], DateTimeFormatter.ISO_DATE);
        // 에약 제한 사항들
        if (studentIds.size() < 2){
            throw new CustomException("최소 2인 이상 예약해야 합니다.", CustomErrorCode.RESERVATION_ONE_PERSON);
        }
        checkReservationOverlaps(startDateTime, endDateTime, startDate);
        checkDuration(startDateTime, endDateTime);
    }

    //2시간 이상, 30분 미만 확인
    public void checkDuration(DateTime startDateTime, DateTime endDateTime) throws CustomException {
        long durationInMillis = endDateTime.getValue() - startDateTime.getValue();
        long twoHoursInMillis = 2 * 60 * 60 * 1000; // 2시간을 밀리초로 변환
        long thirtyMinutesInMillis = 30 * 60 * 1000; // 30분을 밀리초로 변환

        if (durationInMillis > twoHoursInMillis) {
            throw new CustomException("회당 2시간 이상 예약이 불가합니다.", CustomErrorCode.MORE_THAN_2HOURS);
        }

        if (durationInMillis < thirtyMinutesInMillis) {
            throw new CustomException("회당 30분 미만 예약이 불가합니다.", CustomErrorCode.LESS_THAN_30MINUTES);
        }
    }


    // 해당 주에 해당하는 예약만 필터링 (예약 횟수 검사)
    public void checkReservationPerWeek(String studentId, int userId, LocalDate date) {
        System.out.println("student ID " + studentId + " userID " + userId);
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<ReservAssign> reservAssigns = reservAssignRepository.findByUserId(userId);

        long countThisWeek = reservAssigns.stream()
                                .map(ReservAssign::getReservId)
                                .map(reservRepository::findByReservId)
                                .filter(r -> isDateInRange(r.getStartTime(), startOfWeek, endOfWeek))
                                .count();

        System.out.println("Total reservations for student ID " + userId + " is " + countThisWeek);
        String text = studentId + "님이 이번주 예약 횟수를 초과하였습니다.";

        if (countThisWeek >= 2) {
            throw new CustomException(text, CustomErrorCode.MORE_THAN_2APPOINTS);
        }
    }

    //겹치는 예약 있는지 확인
    public void checkReservationOverlaps(DateTime startDateTime, DateTime endDateTime, LocalDate startDate) throws Exception {

        //구글 캘린더에 저장되어 있는 예약 불러오기
//        List<Map<String, Object>> response = calendarService.getDayCalendarReservations(startDate);
        DayReservationResponse response = calendarService.getDayCalendarReservations(startDate);

        if(response != null && response.getReservations() != null){
            for(SingleReservationDto reservation : response.getReservations()){
                DateTime existingStart = new DateTime(reservation.getStart());
                DateTime existingEnd = new DateTime(reservation.getEnd());
                if(startDateTime.getValue() < existingEnd.getValue() && endDateTime.getValue() > existingStart.getValue()){
                    throw new CustomException("이미 예약된 일정이 있습니다.", CustomErrorCode.EVENT_OVERLAPS);
                }
            }
        }
    }

    //예약 기간 제한사항 (한달)
    public void checkReservationPerMonth(LocalDate date) throws CustomException {
        LocalDate today = LocalDate.now();
        LocalDate startOfThisMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfNextMonth = today.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        if (date.isBefore(startOfThisMonth)|| date.isAfter(endOfNextMonth)) {
            throw new CustomException("매달 1일 기준 다음 달까지만 예약이 가능합니다.", CustomErrorCode.OUT_OF_DURATION);
        }
    }

    //삭제 제한 확인 (시작 시간 30분 이후인 경우 삭제불가)
    public void checkDeleteTime(int reservId) throws CustomException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Reserv eventForCheckTime = reservRepository.findByReservId(reservId);
        String eventTime = eventForCheckTime.getStartTime(); //저장된 이벤트 시간 string
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(eventTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        LocalDateTime eventDateTime = zonedDateTime.toLocalDateTime(); //저장된 이벤트 시간 localdatetime

//        System.out.println("eventDateTime : " + eventDateTime);
//        System.out.println("currentDateTime : " + currentDateTime);

        LocalDateTime eventDateTimeAfter = eventDateTime.plusMinutes(30); //저장된 이벤트 시간에 30분 plus

//        System.out.println("eventDateTimeAfter : " + eventDateTimeAfter);

        if (currentDateTime.isAfter(eventDateTimeAfter)) { //현재 시각이 (저장된 이벤트 시작 시간 + 30분)의 이후라면 예약 불가
            throw new CustomException("이미 지난 예약은 삭제할 수 없습니다.", CustomErrorCode.UNABLE_TO_DELETE);
        }
    }

    private boolean isDateInRange(String startTime, LocalDate startOfWeek, LocalDate endOfWeek) {
        LocalDate reservationDate = LocalDate.parse(startTime.split("T")[0], DateTimeFormatter.ISO_DATE);
        return !reservationDate.isBefore(startOfWeek) && !reservationDate.isAfter(endOfWeek);
    }

}

package com.example.MaiN.CalendarService;

import com.example.MaiN.Exception.CustomErrorCode;
import com.example.MaiN.Exception.CustomException;
//import com.example.MaiN.entity.Event;
import com.example.MaiN.entity.Reserv;
import com.example.MaiN.entity.EventAssign;
import com.example.MaiN.repository.ReservAssignRepository;
import com.example.MaiN.repository.ReservRepository;
import com.example.MaiN.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.DateTime;
import okhttp3.internal.http2.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CalendarValidService {

    @Autowired
    private ReservRepository reservRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReservAssignRepository reservAssignRepository;
    @Autowired
    private CalendarGetService calendarGetService;

    public void checkDuration(DateTime startDateTime, DateTime endDateTime) throws CustomException {
        long durationInMillis = endDateTime.getValue() - startDateTime.getValue();
        long twoHoursInMillis = 2 * 60 * 60 * 1000; // 2시간을 밀리초로 변환
        if (durationInMillis > twoHoursInMillis) {
            throw new CustomException("회당 2시간 이상 예약이 불가합니다.", CustomErrorCode.MORE_THAN_2HOURS);
        }
    }
    // 해당 주에 해당하는 예약만 필터링
    public void checkEventsPerWeek(String studentId, int userId, LocalDate date) {
        System.out.println("student ID " + studentId + " userID " + userId);
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<EventAssign> reservAssign = reservAssignRepository.findByUserId(userId);
        List<Integer> reservList = new ArrayList<>();
        for (EventAssign eventAssign : reservAssign) {
            reservList.add(eventAssign.getReservId());
        }
        List<Reserv> reserv = new ArrayList<>();
        for (Integer a : reservList) {
            reserv.add(reservRepository.findByReservId(a));
        }
        long countThisWeek = reserv.stream()
                .filter(r -> {
                    LocalDate reservationDate = LocalDate.parse(r.getStartTime().split("T")[0], DateTimeFormatter.ISO_DATE);
                    return !reservationDate.isBefore(startOfWeek) && !reservationDate.isAfter(endOfWeek);
                })
                .count();
        System.out.println("Total reservations for student ID " + userId + " is " + countThisWeek);
        String text = studentId + "님이 이번주 예약 횟수를 초과하였습니다.";
        if (countThisWeek >= 2) {
            throw new CustomException(text, CustomErrorCode.MORE_THAN_2APPOINTS);
        }
    }

    public void checkEventOverlaps(DateTime startDateTime, DateTime endDateTime, LocalDate startDate) throws Exception {
        ResponseEntity<?> response = calendarGetService.getCalendarEvents(startDate);
        List<Map<String, Object>> existingEventsJson = new ArrayList<>();
        if (response.getBody() instanceof List<?>) {
            List<?> rawList = (List<?>) response.getBody();
            if (!rawList.isEmpty() && rawList.get(0) instanceof Map) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> castedList = (List<Map<String, Object>>) rawList;
                existingEventsJson = castedList;
            }
        }
        if (!existingEventsJson.equals("No Upcoming events found")) {
            ObjectMapper objectMapper = new ObjectMapper();
            for (Map<String, Object> event : existingEventsJson) {
                DateTime existingStart = new DateTime((String) event.get("start"));
                DateTime existingEnd = new DateTime((String) event.get("end"));
                if (startDateTime.getValue() < existingEnd.getValue() && endDateTime.getValue() > existingStart.getValue()) {
                    // 겹치는 이벤트 발견하면 -> 로그 띄움
                    throw new CustomException("이미 예약된 일정이 있습니다.", CustomErrorCode.EVENT_OVERLAPS);
                }
            }
        }
    }
    public void checkEventsPerMonth(LocalDate date) throws CustomException {
        LocalDate today = LocalDate.now();
        LocalDate startOfThisMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfNextMonth = today.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        if (date.isBefore(startOfThisMonth)|| date.isAfter(endOfNextMonth)) {
            throw new CustomException("매달 1일 기준 다음 달까지만 예약이 가능합니다.", CustomErrorCode.OUT_OF_DURATION);
        }
    }

    public void checkDeleteTime(int id) throws CustomException {
        //"2024-06-12T01:00:00.000+09:00"
        LocalDateTime currentDateTime = LocalDateTime.now();
        Reserv eventForCheckTime = reservRepository.findByReservId(id);
        String eventTime = eventForCheckTime.getStartTime(); //저장된 이벤트 시간 string
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(eventTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        LocalDateTime eventDateTime = zonedDateTime.toLocalDateTime(); //저장된 이벤트 시간 localdatetime

        System.out.println("eventDateTime : " + eventDateTime);
        System.out.println("currentDateTime : " + currentDateTime);


        LocalDateTime eventDateTimeAfter = eventDateTime.plusHours(9).plusMinutes(30); //저장된 이벤트 시간에 30분 plus

        System.out.println("eventDateTimeAfter : " + eventDateTimeAfter);

        if (currentDateTime.isAfter(eventDateTimeAfter)) { //현재 시각이 (저장된 이벤트 시작 시간 + 30분)의 이후라면 예약 불가
            throw new CustomException("이미 지난 예약은 삭제할 수 없습니다.", CustomErrorCode.UNABLE_TO_DELETE);
        }
    }

    public void checkAddTime(String startTime, String endTime) throws CustomException {
        ZonedDateTime startZonedDateTime = ZonedDateTime.parse(startTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        ZonedDateTime endZonedDateTime = ZonedDateTime.parse(endTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        LocalDateTime startDateTime = startZonedDateTime.toLocalDateTime();
        LocalDateTime endDateTime = endZonedDateTime.toLocalDateTime();

        Duration duration = Duration.between(startDateTime, endDateTime);
        //System.out.println("차이???! : " + duration);
        long minutes = duration.toMinutes();
        //System.out.println("분 차이????! : " + minutes);

        if (minutes < 30) {
            throw new CustomException("최소 30분 이상 예약해주세요.", CustomErrorCode.UNABLE_TO_ADD);
        }
    }
}

package com.example.MaiN.CalendarService;

import com.example.MaiN.Exception.CustomErrorCode;
import com.example.MaiN.Exception.CustomException;
import com.example.MaiN.entity.Event;
import com.example.MaiN.repository.ReservAssignRepository;
import com.example.MaiN.repository.ReservRepository;
import com.example.MaiN.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.DateTime;
import okhttp3.internal.http2.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    public void checkEventsPerWeek(int userId, LocalDate date){
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        List<Event> reservations = reservRepository.findByUserId(userId);
        long countThisWeek = reservations.stream()
                .filter(r -> {
                    LocalDate reservationDate = LocalDate.parse(r.getStartTime().split("T")[0], DateTimeFormatter.ISO_DATE);
                    return !reservationDate.isBefore(startOfWeek) && !reservationDate.isAfter(endOfWeek);
                })
                .count();

        if (countThisWeek >= 2) {
            throw new CustomException("주당 3회 이상 예약이 불가합니다.", CustomErrorCode.MORE_THAN_2APPOINTS);
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
}

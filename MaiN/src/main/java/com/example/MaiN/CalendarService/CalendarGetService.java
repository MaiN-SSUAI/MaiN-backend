package com.example.MaiN.CalendarService;

import com.example.MaiN.entity.EventAssign;
import com.example.MaiN.entity.Reserv;
import com.example.MaiN.repository.ReservAssignRepository;
import com.example.MaiN.repository.ReservRepository;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class CalendarGetService {
    private static final String CALENDAR_ID = "d4075e67660e0f6bd313a60f05cbb102bc1b2a632c17c1a7e11acc1cf10fd8fe@group.calendar.google.com"; //학부

    @Autowired
    private ReservAssignRepository reservAssignRepository;
    @Autowired
    private ReservRepository reservRepository;

    public String calPixel(DateTime time) {
        Instant instant = Instant.ofEpochMilli(time.getValue());
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.of("Asia/Seoul"));
        int hour = zonedDateTime.getHour();
        int minute = zonedDateTime.getMinute();
        int TotalDivTen = (60 * hour + minute) / 10;
        int result = TotalDivTen * 6;
        return Integer.toString(result);
    }

    public Map<String, Object> toMap(Event event, LocalDate date, int reservId, String purpose) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("reservId", reservId);
        List<String> studentNos = new ArrayList<>();
        map.put("studentNo", studentNos);

        map.put("purpose", purpose); // 예약 목적

        DateTime startDateTime = event.getStart().getDateTime(); // 이벤트의 시작 날짜 및 시간
        DateTime endDateTime = event.getEnd().getDateTime(); // 이벤트의 끝 날짜 및 시간

        map.put("start", event.getStart().getDateTime().toString());
        map.put("end", event.getEnd().getDateTime().toString());

        // DateTime 객체를 Instant 객체로 변환
        Instant startInstant = Instant.ofEpochMilli(startDateTime.getValue());
        Instant endInstant = Instant.ofEpochMilli(endDateTime.getValue());

        // Instant 객체를 LocalDateTime 객체로 변환
        LocalDateTime startLocalDateTime = LocalDateTime.ofInstant(startInstant, ZoneId.of("Asia/Seoul"));
        LocalDateTime endLocalDateTime = LocalDateTime.ofInstant(endInstant, ZoneId.of("Asia/Seoul"));

        // LocalDateTime 객체에서 날짜 부분만 추출하여 LocalDate 객체로 변환
        LocalDate eventStartDate = startLocalDateTime.toLocalDate();
        LocalDate eventEndDate = endLocalDateTime.toLocalDate();

        //입력한 날짜와 이벤트 시작 날짜가 같은 경우 -> startpixel 그대로 계산
        if (date.isEqual(eventStartDate)) {
            map.put("start_pixel", calPixel(event.getStart().getDateTime()));
        }
        //입력한 날짜보다 시작 날짜가 빠른 경우 -> start_pixel = 0
        else if (date.isAfter(eventStartDate)) {
            map.put("start_pixel", "0");
        }
        //입력한 날짜와 이벤트 끝 날짜가 같은 경우 ->end pixel 그대로 계산
        if (date.isEqual(eventEndDate)) {
            map.put("end_pixel", calPixel(event.getEnd().getDateTime()));
        }
        //입력한 날짜보다 이벤트 끝 날짜가 느린 경우 (입력 날짜에 이벤트가 끝나지 않은 경우) -> end pixel = 11:59 에 대하여 계산
        else if (date.isBefore(eventEndDate)) {
            map.put("end_pixel", "864");
        }
        return map;
    }

    //GET
    public ResponseEntity<?> getCalendarEvents(LocalDate date) throws Exception {
        //Calendar 객체 생성
        Calendar service = CalendarApproach.getCalendarService();

        // 입력받은 날짜를 이용해 그 날의 시작 시간과 끝 시간을 DateTime 형식으로 변환
        DateTime startOfDay = new DateTime(date.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli());
        DateTime endOfDay = new DateTime(date.plusDays(1).atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli());

        // 캘린더에서 날짜 범위에 해당하는 이벤트들을 가져옴
        Events events = service.events().list(CALENDAR_ID)
                .setTimeMin(startOfDay)
                .setTimeMax(endOfDay)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        // 가져온 이벤트들을 리스트에 저장
        List<Event> eventsList = events.getItems();

        List<Map<String, Object>> allEventList = new ArrayList<>();


        for (Event event : eventsList) {
            String summary = event.getSummary();
            // 특정 문자열이 포함된 장소에 예약된 이벤트만 필터링
            String[] parts = summary.split("/");
            if (parts.length > 0 && parts[0].contains("2")) { //세미나실 2  event 라면
                Reserv dbEvent = reservRepository.findByEventId(event.getId()); //event id 로 reserv 테이블에 저장된 예약 가져오기
                int reservId = (dbEvent != null) ? dbEvent.getId() : 0;  //db 에 없으면 reservId = 0

                Optional<Reserv> mainEvent = reservRepository.findById(reservId);
                String purpose = mainEvent.map(Reserv::getPurpose).orElse(""); //purpose 추가

                // 이벤트를 맵으로 변환
                Map<String, Object> eventMap = toMap(event, date, reservId, purpose);
                String studentNo = parts[1].replace("[", "").replace("]", "").trim();
                List<String> studentNoList = Arrays.asList(studentNo.split(", "));
                eventMap.put("studentNo", studentNoList);

                allEventList.add(eventMap);
            }
        }
        return ResponseEntity.ok(allEventList);
    }

    public ResponseEntity<?> getWeekCalendarEvents(LocalDate date) throws Exception {
        Calendar service = CalendarApproach.getCalendarService();
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        Map<String, List<Map<String, Object>>> weeklyEvents = new LinkedHashMap<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            weeklyEvents.put(day.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).substring(0, 3), new ArrayList<>());
        }

        ExecutorService executor = Executors.newFixedThreadPool(7);
        List<Future<Map<String, List<Map<String, Object>>>>> futures = new ArrayList<>();

        for (LocalDate currentDate = startOfWeek; !currentDate.isAfter(endOfWeek); currentDate = currentDate.plusDays(1)) {
            LocalDate finalCurrentDate = currentDate;
            Future<Map<String, List<Map<String, Object>>>> future = executor.submit(() -> {
                DateTime startOfDay = new DateTime(finalCurrentDate.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli());
                DateTime endOfDay = new DateTime(finalCurrentDate.plusDays(1).atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli());

                Events events = service.events().list(CALENDAR_ID)
                        .setTimeMin(startOfDay)
                        .setTimeMax(endOfDay)
                        .setOrderBy("startTime")
                        .setSingleEvents(true)
                        .execute();

                List<Event> eventsList = events.getItems();
                List<Map<String, Object>> dayEventsList = new ArrayList<>();

                for (Event event : eventsList) {
                    String summary = event.getSummary();
                    String[] parts = summary.split("/");
                    if (parts.length > 0 && parts[0].contains("2")) {
                        Reserv dbEvent = reservRepository.findByEventId(event.getId());
                        int reservId = (dbEvent != null) ? dbEvent.getId() : 0;

                        Optional<Reserv> mainEvent = reservRepository.findById(reservId);
                        String purpose = mainEvent.map(Reserv::getPurpose).orElse("");

                        Map<String, Object> eventMap = toMap(event, finalCurrentDate, reservId, purpose);
                        String studentNo = parts[1].replace("[", "").replace("]", "").trim();
                        List<String> studentNoList = Arrays.asList(studentNo.split(", "));
                        eventMap.put("studentNo", studentNoList);

                        dayEventsList.add(eventMap);
                    }
                }

                String dayOfWeek = finalCurrentDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).substring(0, 3);
                Map<String, List<Map<String, Object>>> dayEvents = new LinkedHashMap<>();
                dayEvents.put(dayOfWeek, dayEventsList);

                return dayEvents;
            });
            futures.add(future);
        }

        for (Future<Map<String, List<Map<String, Object>>>> future : futures) {
            Map<String, List<Map<String, Object>>> dayEvents = future.get();
            for (Map.Entry<String, List<Map<String, Object>>> entry : dayEvents.entrySet()) {
                weeklyEvents.get(entry.getKey()).addAll(entry.getValue());
            }
        }

        executor.shutdown();
        return ResponseEntity.ok(weeklyEvents);
    }
}

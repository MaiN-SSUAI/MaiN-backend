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
import org.springframework.cglib.core.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
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

    public String calPixel(LocalDateTime time) {
//        Instant instant = Instant.ofEpochMilli(time.getValue());
//        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.of("Asia/Seoul"));
//        int hour = zonedDateTime.getHour();
//        int minute = zonedDateTime.getMinute();
//        int TotalDivTen = (60 * hour + minute) / 10;
//        int result = TotalDivTen * 6;
//        return Integer.toString(result);

        int hour = time.getHour();
        int minute = time.getMinute();
        int TotalDiven = (60 * hour + minute) / 10;
        int result = TotalDiven * 6;
        return Integer.toString(result);
    }

    public Map<String, Object> toMap(Event event, LocalDate date, int reservId, String purpose) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("reservId", reservId);
        List<String> studentNos = new ArrayList<>();
        map.put("studentNo", studentNos);

        map.put("purpose", purpose); // 예약 목적

        DateTime startDateTime = event.getStart().getDateTime();
        DateTime endDateTime = event.getEnd().getDateTime();

        // DateTime이 null인 경우 날짜만 있는 all-day 이벤트로 간주하여 시간 설정
        LocalDateTime startLocalDateTime = (startDateTime != null)
                ? LocalDateTime.ofInstant(Instant.ofEpochMilli(startDateTime.getValue()), ZoneId.of("Asia/Seoul")) //기본 일정
                : LocalDate.parse(event.getStart().getDate().toStringRfc3339()).atTime(0, 0); //all day event

        LocalDateTime endLocalDateTime = (endDateTime != null)
                ? LocalDateTime.ofInstant(Instant.ofEpochMilli(endDateTime.getValue()), ZoneId.of("Asia/Seoul"))
                : LocalDate.parse(event.getEnd().getDate().toStringRfc3339()).minusDays(1).atTime(23, 59);

        // 포맷 설정 후 map에 추가
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        map.put("start", startLocalDateTime.atOffset(ZoneOffset.ofHours(9)).format(formatter));
        map.put("end", endLocalDateTime.atOffset(ZoneOffset.ofHours(9)).format(formatter));

        // LocalDateTime 객체에서 날짜 부분만 추출하여 LocalDate 객체로 변환
        LocalDate eventStartDate = startLocalDateTime.toLocalDate();
        LocalDate eventEndDate = endLocalDateTime.toLocalDate();

        //입력한 날짜와 이벤트 시작 날짜가 같은 경우 -> startpixel 그대로 계산
        if (date.isEqual(eventStartDate)) {
            map.put("start_pixel", calPixel(startLocalDateTime));
        }
        //입력한 날짜보다 시작 날짜가 빠른 경우 -> start_pixel = 0
        else if (date.isAfter(eventStartDate)) {
            map.put("start_pixel", "0");
        }
        //입력한 날짜와 이벤트 끝 날짜가 같은 경우 ->end pixel 그대로 계산
        if (date.isEqual(eventEndDate)) {
            map.put("end_pixel", calPixel(endLocalDateTime));
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

            if (summary.replaceAll("\\s+", "").contains("세미나실2")) { //세미나실 2 관련 event 라면

                String pattern = "^세미나실2/.*$"; //학생들이 예약했을 때의 형식 ( "세미나실2/" 로 시작하는 패턴)
                if (summary.matches(pattern)) { //학생들의 예약인 경우

                    String[] parts = summary.split("/");

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
                // 제목이 "세미나실2/[학번]" 형식이 아닌 일정
                else {
                    int reservId = 0;
                    String purpose = "";
                    List<String> studentNoList = List.of(summary);

                    Map<String, Object> eventMap = toMap(event, date, reservId, purpose);
                    eventMap.put("studentNo", studentNoList);
                    allEventList.add(eventMap);
                }
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

                    if (summary.replaceAll("\\s+", "").contains("세미나실2")) { //세미나실 2 관련 event 라면
                        String pattern = "^세미나실2/.*$"; //학생들이 예약했을 때의 형식 ( "세미나실2/" 로 시작하는 패턴)
                        if(summary.matches(pattern)){

                            String[] parts = summary.split("/");

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
                        else{
                            int reservId = 0;
                            String purpose = "";
                            Map<String, Object> eventMap = toMap(event, finalCurrentDate, reservId, purpose);
                            List<String> studentNoList = List.of(summary);
                            eventMap.put("studentNo", studentNoList);

                            dayEventsList.add(eventMap);
                        }

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

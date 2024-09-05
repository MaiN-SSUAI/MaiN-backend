package com.example.MaiN.service;

import com.example.MaiN.entity.Reserv;
import com.example.MaiN.repository.ReservRepository;
import com.example.MaiN.security.GoogleCredentialProvider;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class CalendarService {
    private static final String APPLICATION_NAME = "Google Calendar API";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${calendar.id}")
    private static String CALENDAR_ID;

    private static ReservRepository reservRepository;
    public CalendarService(ReservRepository reservRepository){
        this.reservRepository = reservRepository;
    }

    private static Calendar getCalendarService() throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, GoogleCredentialProvider.getCredential())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    // 프론트 처리용 픽셀 계산
    private static String calPixel(DateTime time) {
        Instant instant = Instant.ofEpochMilli(time.getValue());
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.of("Asia/Seoul"));
        int hour = zonedDateTime.getHour();
        int minute = zonedDateTime.getMinute();
        int TotalDivTen = (60 * hour + minute) / 10;
        int result = TotalDivTen * 6;
        return Integer.toString(result);
    }

    // reservation 객체를 map으로 변환
    private static Map<String, Object> toMap(Event event, LocalDate date, int reservId, String purpose, List<String> studentNoList) {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("reservId", reservId);
        map.put("studentNo", studentNoList);
        map.put("purpose", purpose);
        map.put("start", event.getStart().getDateTime().toString());
        map.put("end", event.getEnd().getDateTime().toString());

        // DateTime 객체에서 LocalDate로 변환 (2024-09-03T10:15:00+09:00 -> 2024-09-03)
        // 이벤트 시작 날짜 -> YYYY-MM-DD
        LocalDate eventStartDate = Instant.ofEpochMilli(event.getStart().getDateTime().getValue())
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDate();
        // 이벤트 끝 날짜 -> YYYY-MM-DD
        LocalDate eventEndDate = Instant.ofEpochMilli(event.getEnd().getDateTime().getValue())
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDate();

        // 입력한 날짜와 이벤트 시작 날짜가 같은 경우 -> startpixel 그대로 계산
        if (date.isEqual(eventStartDate)) {
            map.put("start_pixel", calPixel(event.getStart().getDateTime()));
        }
        // 입력한 날짜보다 시작 날짜가 빠른 경우 -> start_pixel = 0
        else if (date.isAfter(eventStartDate)) {
            map.put("start_pixel", "0");
        }
        // 입력한 날짜와 이벤트 끝 날짜가 같은 경우 -> end pixel 그대로 계산
        if (date.isEqual(eventEndDate)) {
            map.put("end_pixel", calPixel(event.getEnd().getDateTime()));
        }
        // 입력한 날짜보다 이벤트 끝 날짜가 느린 경우 (입력 날짜에 이벤트가 끝나지 않은 경우) -> end pixel = 11:59 에 대하여 계산
        else if (date.isBefore(eventEndDate)) {
            map.put("end_pixel", "864");
        }

        return map;
    }

    // 캘린더에 예약 등록
    public String addReservation(List studentIds, String startDateTimeStr, String endDateTimeStr) throws Exception {
        Calendar calendar = getCalendarService();

        //EventDateTime 객체 생성
        DateTime startDateTime = new DateTime(startDateTimeStr);
        DateTime endDateTime = new DateTime(endDateTimeStr);
        EventDateTime startEventDateTime = new EventDateTime().setDateTime(startDateTime).setTimeZone("Asia/Seoul");
        EventDateTime endEventDateTime = new EventDateTime().setDateTime(endDateTime).setTimeZone("Asia/Seoul");

        // 일정 제목 설정 -> "세미나실2 / [20220000, 20221111]"
        String summary = String.format("세미나실2%s", studentIds);

        // Event 객체 생성
        Event event = new Event()
                .setSummary(summary)
                .setStart(startEventDateTime)
                .setEnd(endEventDateTime);

        // 구글캘린더에 예약 등록
        event = calendar.events().insert(CALENDAR_ID, event).execute();

        return event.getId();
    }

    // 캘린더에서 예약 삭제
    public void deleteReservation(String eventId) throws Exception {
        Calendar calendar = getCalendarService();

        // 구글캘린더에서 예약 삭제
        calendar.events().delete(CALENDAR_ID, eventId).execute();
    }

    public DateTime updateReservation(String eventId, List studentIds, String startDateTimeStr, String endDateTimeStr) throws Exception {
        //예약 수정 로직
        Calendar calendar = getCalendarService();
        Event event = calendar.events().get(CALENDAR_ID, eventId).execute();
        String summary = String.format("세미나실2%s", studentIds);

        //EventDateTime 객체 생성
        DateTime startDateTime = new DateTime(startDateTimeStr);
        DateTime endDateTime = new DateTime(endDateTimeStr);
        EventDateTime startEventDateTime = new EventDateTime().setDateTime(startDateTime).setTimeZone("Asia/Seoul");
        EventDateTime endEventDateTime = new EventDateTime().setDateTime(endDateTime).setTimeZone("Asia/Seoul");

        // 기존 일정에 업데이트
        event.setSummary(summary)
                .setStart(startEventDateTime)
                .setEnd(endEventDateTime);

        // 구글캘린더에 예약 등록
        Event updatedEvent = calendar.events().update(CALENDAR_ID, eventId, event).execute();

        return updatedEvent.getUpdated();
    }

    // 세미나실 2 필터링, 대학원생 예약과 학부생 예약 처리, map으로 변환하는 예약 필터링 및 처리 메서드
    private static Map<String, Object> filterReservation(List<Event> eventsList, LocalDate date) {

        Map<String, Object> eventMap = Map.of();

        for (Event event : eventsList) {
            String[] parts = event.getSummary().split("/");

            //세미나실2만 필터링
            if (parts.length > 0 && parts[0].contains("2")) {
                //EventId를 통해 데이터베이스에 존재하는 이벤트를 필터링
                Reserv dbEvent = reservRepository.findByEventId(event.getId());

                //데이터베이스에 존재하는 이벤트일 경우, getId() 아닐 경우, 0으로 세팅
                int reservId = (dbEvent != null) ? dbEvent.getId() : 0;
                //데이터베이스에 존재하는 이벤트일 경우, getPurpose() 아닐 경우, ""으로 세팅
                String purpose = (dbEvent != null) ? dbEvent.getPurpose() : "";

                //학번 하나씩 리스트로 저장
                List<String> studentNoList = Arrays.asList(parts[1].replace("[", "").replace("]", "").trim().split(", "));

                // 이벤트를 map으로 변환
                eventMap = toMap(event, date, reservId, purpose, studentNoList);

            }
        }
        return eventMap;
    }

    // 캘린더에서 하루 치 예약 가져오기
    public static List<Map<String, Object>> getDayCalendarReservations(LocalDate date) throws Exception {

        Calendar calendar = getCalendarService();

        // 입력받은 날짜를 이용해 그 날의 시작 시간과 끝 시간을 DateTime 형식으로 변환
        DateTime startOfDay = new DateTime(date.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli());
        DateTime endOfDay = new DateTime(date.plusDays(1).atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli());

        // 캘린더에서 날짜 범위에 해당하는 이벤트들 get
        Events events = calendar.events().list(CALENDAR_ID)
                .setTimeMin(startOfDay)
                .setTimeMax(endOfDay)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        // 구글캘린더에서 가져온 이벤트들
        List<Event> eventsList = events.getItems();

        // 가져온 이벤트들을 처리해서 리스트에 저장
        List<Map<String, Object>> dayEventList = new ArrayList<>();

        Map<String, Object> eventMap = filterReservation(eventsList, date);

        dayEventList.add(eventMap);

        return dayEventList;
    }

    // 캘린더에서 일주일 치 예약 가져오기
    public Map<String, List<Map<String, Object>>> getWeekCalendarReservations(LocalDate date) throws Exception {
        Calendar calendar = getCalendarService();

        // 요일별 이벤트 리스트를 담는 Map 객체 생성
        Map<String, List<Map<String, Object>>> weeklyEvents = new LinkedHashMap<>();

        for (DayOfWeek day : DayOfWeek.values()) {
            // 요일의 짧은 이름 가져오기 ex.)MON, TUE ...
            weeklyEvents.put(day.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).substring(0, 3), new ArrayList<>());
        }
        // 스레드풀 생성
        ExecutorService executor = Executors.newFixedThreadPool(7);
        List<Future<Map<String, List<Map<String, Object>>>>> futures = new ArrayList<>();

        // 예약 날짜에 해당하는 주의 월요일(startOfWeek), 일요일(endOfWeek) 계산
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // 월요일부터 일요일까지 반복
        for (LocalDate currentDate = startOfWeek; !currentDate.isAfter(endOfWeek); currentDate = currentDate.plusDays(1)) {
            LocalDate finalCurrentDate = currentDate;
            // 비동기 작업 제출
            Future<Map<String, List<Map<String, Object>>>> future = executor.submit(() -> {

                // 해당 날짜의 시작 시간과 끝 시간
                DateTime startOfDay = new DateTime(finalCurrentDate.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli());
                DateTime endOfDay = new DateTime(finalCurrentDate.plusDays(1).atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli());

                // 예약 목록 조회
                Events events = calendar.events().list(CALENDAR_ID)
                        .setTimeMin(startOfDay)
                        .setTimeMax(endOfDay)
                        .setOrderBy("startTime")
                        .setSingleEvents(true)
                        .execute();

                // 구글캘린더에서 가져온 이벤트들
                List<Event> eventsList = events.getItems();

                // 가져온 이벤트들을 처리해서 리스트에 저장
                List<Map<String, Object>> dayEventList = new ArrayList<>();

                Map<String, Object> eventMap = filterReservation(eventsList, date);

                dayEventList.add(eventMap);

                // 비동기 결과 저장
                String dayOfWeek = finalCurrentDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).substring(0, 3);
                Map<String, List<Map<String, Object>>> dayEvents = new LinkedHashMap<>();

                // 요일을 키로, 예약 목록 저장
                dayEvents.put(dayOfWeek, dayEventList);

                return dayEvents;
            });
            futures.add(future);
        }

        // 비동기 결과 취합
        for (Future<Map<String, List<Map<String, Object>>>> future : futures) {
            Map<String, List<Map<String, Object>>> dayEvents = future.get();
            for (Map.Entry<String, List<Map<String, Object>>> entry : dayEvents.entrySet()) {
                weeklyEvents.get(entry.getKey()).addAll(entry.getValue());
            }
        }

        // 스레드풀 종료
        executor.shutdown();
        return weeklyEvents;
    }

}
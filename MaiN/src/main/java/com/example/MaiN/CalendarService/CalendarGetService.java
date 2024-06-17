package com.example.MaiN.CalendarService;

import com.example.MaiN.entity.EventAssign;
import com.example.MaiN.repository.ReservAssignRepository;
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

@Service
public class CalendarGetService {
    private static final String CALENDAR_ID = "c_9pdatu4vq4b02h0ua44unu33es@group.calendar.google.com"; //학부

    @Autowired
    private ReservAssignRepository reservAssignRepository;

    public String calPixel(DateTime time) {
        Instant instant = Instant.ofEpochMilli(time.getValue());
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.of("Asia/Seoul"));
        int hour = zonedDateTime.getHour();
        int minute = zonedDateTime.getMinute();
        int TotalDivTen = (60 * hour + minute) / 10;
        int result = TotalDivTen * 6;
        return Integer.toString(result);
    }

    public Map<String, Object> toMap(Event event, LocalDate date, int reservId) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("reservId", reservId);
        List<String> studentNos = new ArrayList<>();
        map.put("studentNo", studentNos);

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
            map.put("end_pixel","864");
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

        Map<Integer, Map<String, Object>> useGoogleEventsMap = new LinkedHashMap<>(); // useGoogleEventsMap 은 reservId가 0으로 객체 하나로 합칠 필요 X
        List<Map<String, Object>> useAppEventsList = new ArrayList<>(); // useAppEventsList는 여러명 예약으로 객체를 하나로 합칠 필요 O

        for (Event event : eventsList) {
            String summary = event.getSummary();
            // 특정 문자열이 포함된 장소에 예약된 이벤트만 필터링
            String[] parts = summary.split("/");
            if (parts.length > 0 && parts[0].contains("2")) {
                EventAssign dbEvent = reservAssignRepository.findByEventId(event.getId());
                int reservId = (dbEvent != null) ? dbEvent.getReservId() : 0;

                // 이벤트를 맵으로 변환
                Map<String, Object> eventMap = toMap(event, date, reservId);
                String studentNo = parts[1];

                // 동일한 reservId를 가진 기존 이벤트가 이미 맵에 있는 경우
                if (reservId == 0) {
                    List<String> studentNos = new ArrayList<>();
                    studentNos.add(studentNo);
                    eventMap.put("studentNo", studentNos);
                    useAppEventsList.add(eventMap);
                } else {
                    // 동일한 reservId를 가진 기존 이벤트가 이미 맵에 있는 경우
                    if (useGoogleEventsMap.containsKey(reservId)) {
                        Map<String, Object> existingEventMap = useGoogleEventsMap.get(reservId);
                        List<String> studentNos = (List<String>) existingEventMap.get("studentNo");
                        studentNos.add(studentNo);
                    } else {
                        List<String> studentNos = new ArrayList<>();
                        studentNos.add(studentNo);
                        eventMap.put("studentNo", studentNos);
                        useGoogleEventsMap.put(reservId, eventMap);
                    }
                }
            }
        }

        // 리스트로 변환
        List<Map<String, Object>> allEventsList = new ArrayList<>(useGoogleEventsMap.values());
        allEventsList.addAll(useAppEventsList);

        return ResponseEntity.ok(allEventsList);
    }

    public ResponseEntity<?> getWeekCalendarEvents(LocalDate date) throws Exception {
        // 구글 캘린더 서비스에 접근할 수 있는 Calendar 객체 생성
        Calendar service = CalendarApproach.getCalendarService();

        // startOfWeek = 월요일, endOfWeek = 일요일
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // 요일별 이벤트를 저장할 맵
        Map<String, List<Map<String, Object>>> weeklyEvents = new LinkedHashMap<>();
        weeklyEvents.put("Mon", new ArrayList<>());
        weeklyEvents.put("Tue", new ArrayList<>());
        weeklyEvents.put("Wed", new ArrayList<>());
        weeklyEvents.put("Thu", new ArrayList<>());
        weeklyEvents.put("Fri", new ArrayList<>());
        weeklyEvents.put("Sat", new ArrayList<>());
        weeklyEvents.put("Sun", new ArrayList<>());

        // 날짜 하나씩 돌기
        for (LocalDate currentDate = startOfWeek; !currentDate.isAfter(endOfWeek); currentDate = currentDate.plusDays(1)) {
            DateTime startOfDay = new DateTime(currentDate.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli());
            DateTime endOfDay = new DateTime(currentDate.plusDays(1).atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli());

            Events events = service.events().list(CALENDAR_ID)
                    .setTimeMin(startOfDay)
                    .setTimeMax(endOfDay)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            // 가져온 이벤트들을 리스트에 저장
            List<Event> eventsList = events.getItems();

            Map<Integer, Map<String, Object>> useGoogleEventsMap = new LinkedHashMap<>(); // useGoogleEventsMap 은 reservId가 0으로 객체 하나로 합칠 필요 X
            List<Map<String, Object>> useAppEventsList = new ArrayList<>(); // useAppEventsList는 여러명 예약으로 객체를 하나로 합칠 필요 O

            for (Event event : eventsList) {
                String summary = event.getSummary();

                String[] parts = summary.split("/");
                if (parts.length > 0 && parts[0].contains("2")) {
                    EventAssign dbEvent = reservAssignRepository.findByEventId(event.getId());
                    int reservId = (dbEvent != null) ? dbEvent.getReservId() : 0;

                    // 이벤트를 맵으로 변환
                    Map<String, Object> eventMap = toMap(event, currentDate, reservId);
                    String studentNo = parts[1];

                    if (reservId == 0) {
                        List<String> studentNos = new ArrayList<>();
                        studentNos.add(studentNo);
                        eventMap.put("studentNo", studentNos);
                        useAppEventsList.add(eventMap);
                    } else {
                        if (useGoogleEventsMap.containsKey(reservId)) {
                            Map<String, Object> existingEventMap = useGoogleEventsMap.get(reservId);
                            List<String> studentNos = (List<String>) existingEventMap.get("studentNo");
                            studentNos.add(studentNo);
                        } else {
                            List<String> studentNos = new ArrayList<>();
                            studentNos.add(studentNo);
                            eventMap.put("studentNo", studentNos);
                            useGoogleEventsMap.put(reservId, eventMap);
                        }
                    }
                }
            }
            // 리스트에 합치기
            String dayOfWeek = currentDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).substring(0, 3);
            weeklyEvents.get(dayOfWeek).addAll(useGoogleEventsMap.values());
            weeklyEvents.get(dayOfWeek).addAll(useAppEventsList);
        }

        return ResponseEntity.ok(weeklyEvents);
    }
}

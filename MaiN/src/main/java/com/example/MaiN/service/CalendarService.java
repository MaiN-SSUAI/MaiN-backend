package com.example.MaiN.service;


import com.example.MaiN.dto.UserDto;
import com.example.MaiN.entity.User;
import com.example.MaiN.repository.ReservAssignRepository;
import com.example.MaiN.repository.ReservRepository;
import com.example.MaiN.repository.UserRepository;
import com.example.MaiN.entity.EventAssign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;
import com.example.MaiN.controller.CalendarController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.Map;
import java.time.format.DateTimeFormatter;

@Service
public class CalendarService {
    private static final String APPLICATION_NAME = "Google Calendar API";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    //    private static final String SERVICE_ACCOUNT_KEY_PATH = "reservecalendar-410115-141fd088c697.json";
    private static final String CALENDAR_ID = "c_9pdatu4vq4b02h0ua44unu33es@group.calendar.google.com"; //학부꺼
    //    private static final String CALENDAR_ID = "maintest39@gmail.com"; //테스트 계정 캘린더
    @Autowired
    private ReservRepository reservRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReservAssignRepository reservAssignRepository;

    //API사용을 위한 인증 정보를 가져오는 메서드
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = CalendarController.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }

        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public class CustomException extends RuntimeException {
        public CustomException(String message) {
            super(message);
        }
    }
    @Component
    public class CustomErrorAttributes extends DefaultErrorAttributes {

        @Override
        public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
            Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);
            Throwable error = getError(webRequest);

            // "message" 필드 추가
            if (error instanceof CustomException) {
                errorAttributes.put("message", error.getMessage());
            }

            return errorAttributes;
        }
    }

    //구글 캘린더 서비스에 접근할 수 있는 Calendar 객체 생성
    public Calendar getCalendarService() throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }


    public String calPixel(DateTime time) {
        Instant instant = Instant.ofEpochMilli(time.getValue());
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.of("Asia/Seoul"));
        int hour = zonedDateTime.getHour();
        int minute = zonedDateTime.getMinute();
        int TotalDivTen = (60 * hour + minute) / 10;
        int result = TotalDivTen * 6;
        return Integer.toString(result);
    }

    public Map<String, Object> toMap(List items, Event event, LocalDate date, int reservId) {
        Map<String, Object> map = new HashMap<>();
        map.put("summary", event.getSummary());
        map.put("start", event.getStart().getDateTime().toString());
        map.put("end", event.getEnd().getDateTime().toString());
        map.put("eventId", event.getId());
        map.put("reservId", reservId);

        DateTime startDateTime = event.getStart().getDateTime(); // 이벤트의 시작 날짜 및 시간
        DateTime endDateTime = event.getEnd().getDateTime(); // 이벤트의 끝 날짜 및 시간

        // DateTime 객체를 Instant 객체로 변환
        Instant startInstant = Instant.ofEpochMilli(startDateTime.getValue());
        Instant endInstant = Instant.ofEpochMilli(endDateTime.getValue());

        // Instant 객체를 LocalDateTime 객체로 변환
        LocalDateTime startLocalDateTime = LocalDateTime.ofInstant(startInstant, ZoneId.of("Asia/Seoul"));
        LocalDateTime endLocalDateTime = LocalDateTime.ofInstant(endInstant, ZoneId.of("Asia/Seoul"));

        // LocalDateTime 객체에서 날짜 부분만 추출하여 LocalDate 객체로 변환
        LocalDate eventStartDate = startLocalDateTime.toLocalDate();
        LocalDate eventEndDate = endLocalDateTime.toLocalDate();

        //11시 59분
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59));
        DateTime timeAt1159 = new DateTime(dateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli());

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
            map.put("end_pixel", calPixel(timeAt1159));
        }
        return map;
    }

    //GET
    public ResponseEntity<?> getCalendarEvents(LocalDate date) throws Exception {
        //구글 캘린더 서비스에 접근할 수 있는 Calendar 객체 생성
        Calendar service = getCalendarService();

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
        //가져온 이벤트들을 리스트에 저장
        List<Event> eventsList = events.getItems();

        List<Map<String, Object>> eventsMapList = new ArrayList<>();

        for (Event event : eventsList) {
            String summary = event.getSummary();
            // 특정 문자열이 포함된 장소에 예약된 이벤트만 필터링
            String[] parts = summary.split("/");
            if (parts.length > 0 && parts[0].contains("1")) {
                EventAssign dbEvent = reservAssignRepository.findByEventId(event.getId());
                if (dbEvent != null) {
                    int reservId = dbEvent.getReservId();
                    eventsMapList.add(toMap(eventsList, event, date, reservId));
                } else {
                    int reservId = 0;
                    eventsMapList.add(toMap(eventsList, event, date, reservId));
                }
            }
        }
        return ResponseEntity.ok(eventsMapList);
    }

    private void checkDuration(DateTime startDateTime, DateTime endDateTime) throws CustomException {
        long durationInMillis = endDateTime.getValue() - startDateTime.getValue();
        long twoHoursInMillis = 2 * 60 * 60 * 1000; // 2시간을 밀리초로 변환
        if (durationInMillis > twoHoursInMillis) {
            throw new CustomException("More than 2 hours");
        }
    }

    // 해당 주에 해당하는 예약만 필터링

    private void checkEventsPerWeek(int userId, LocalDate date){
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        List<com.example.MaiN.entity.Event> reservations = reservRepository.findByUserId(userId);
        long countThisWeek = reservations.stream()
                .filter(r -> {
                    LocalDate reservationDate = LocalDate.parse(r.getStartTime().split("T")[0], DateTimeFormatter.ISO_DATE);
                    return !reservationDate.isBefore(startOfWeek) && !reservationDate.isAfter(endOfWeek);
                })
                .count();

            if (countThisWeek >= 2) {
                throw new CustomException("More than 2 appointments a week");
            }
    }

    private void checkEventOverlaps(DateTime startDateTime, DateTime endDateTime, LocalDate startDate) throws Exception {
        ResponseEntity<?> response = getCalendarEvents(startDate);
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
                    throw new CustomException("Event Overlaps");
                }
            }
        }
    }

    private void checkEventsPerMonth(LocalDate date) throws CustomException {
        LocalDate today = LocalDate.now();
        LocalDate startOfThisMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfNextMonth = today.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        if (date.isBefore(startOfThisMonth)|| date.isAfter(endOfNextMonth)) {
            throw new CustomException("Reservation can only be made for this month and the next month");
        }
    }

    // 예약 추가하기 (주최자)
    public String addOrganizeEvent(String studentId, int userId, String purpose, String startDateTimeStr, String endDateTimeStr) throws Exception {
        Calendar service = getCalendarService();
        DateTime startDateTime = new DateTime(startDateTimeStr);
        DateTime endDateTime = new DateTime(endDateTimeStr);
        LocalDate startDate = LocalDate.parse(startDateTimeStr.split("T")[0], DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(endDateTimeStr.split("T")[0], DateTimeFormatter.ISO_DATE);
        // 에약 제한 사항들
        checkDuration(startDateTime, endDateTime);
        checkEventOverlaps(startDateTime, endDateTime, startDate);
        System.out.println("Total reservations for student ID " + userId + " from " + startDate + startDateTime + " to " + endDate + endDateTime);
        String summary = String.format("세미나실1/%s", studentId);
        Event event = new Event().setSummary(summary);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Asia/Seoul");
        event.setStart(start);
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Asia/Seoul");
        event.setEnd(end);
        event = service.events().insert(CALENDAR_ID, event).execute();
        return event.getId();
    }

    // 예약 추가하기 (주최자 제외 팀원들)
    public String addEvent(String studentId, int userId, String purpose, String startDateTimeStr, String endDateTimeStr) throws Exception {
        Calendar service = getCalendarService();
        DateTime startDateTime = new DateTime(startDateTimeStr);
        DateTime endDateTime = new DateTime(endDateTimeStr);

        String summary = String.format("세미나실1/%s", studentId);
        Event event = new Event().setSummary(summary);

        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Asia/Seoul");
        event.setStart(start);

        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Asia/Seoul");
        event.setEnd(end);

        event = service.events().insert(CALENDAR_ID, event).execute();
        return event.getId();
    }

    public ResponseEntity<?> checkUser(String studentId, LocalDate date) {

        Optional<User> userOptional = userRepository.findByStudentNo(studentId);
        User user = userOptional.orElse(null);

        if(user == null){
            return ResponseEntity.ok("uninformed/valid user");
        }
        int userId = user.getId();
        checkEventsPerMonth(date);
        checkEventsPerWeek(userId, date);
        return ResponseEntity.ok("informed/valid user");
    }

    public void deleteCalendarEvents(String eventId) throws Exception {
        //구글 캘린더 서비스에 접근할 수 있는 Calendar 객체 생성
        Calendar service = getCalendarService();
        service.events().delete(CALENDAR_ID, eventId).execute();
    }

    public String updateCalendarEvents(String location,List<String> studentId, String startDateTimeStr, String endDateTimeStr, String eventId) throws Exception {
        //구글 캘린더 서비스에 접근할 수 있는 Calendar 객체 생성
        Calendar service = getCalendarService();

        Event event = service.events().get(CALENDAR_ID, eventId).execute();

        String summary = String.format("%s/%s",location, studentId);
        event.setSummary(summary);

        DateTime startDateTime = new DateTime(startDateTimeStr);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Asia/Seoul");
        event.setStart(start);

        DateTime endDateTime = new DateTime(endDateTimeStr);
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Asia/Seoul");
        event.setEnd(end);

        Event updatedEvent = service.events().update(CALENDAR_ID, eventId, event).execute();
        return updatedEvent.getId();
    }
}


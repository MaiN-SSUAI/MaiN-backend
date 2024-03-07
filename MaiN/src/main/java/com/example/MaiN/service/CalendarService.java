package com.example.MaiN.service;

import com.example.MaiN.controller.CalendarController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
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
import com.google.auth.oauth2.GoogleCredentials;
import org.aspectj.apache.bcel.util.ClassPath;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.security.GeneralSecurityException;
import java.time.*;
import java.util.*;
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
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        return credential;
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

    //GET
    public String getCalendarEvents(String date, String location) throws Exception {
        //구글 캘린더 서비스에 접근할 수 있는 Calendar 객체 생성
        Calendar service = getCalendarService();

        //입력받은 날짜를 local date 형식으로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);

        // 입력받은 날짜를 이용해 그 날의 시작 시간과 끝 시간을 DateTime 형식으로 변환
        DateTime startOfDay = new DateTime(localDate.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli());
        DateTime endOfDay = new DateTime(localDate.plusDays(1).atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli());

        // 캘린더에서 날짜 범위에 해당하는 이벤트들을 가져옴
        Events events = service.events().list(CALENDAR_ID)
                .setTimeMin(startOfDay)
                .setTimeMax(endOfDay)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        //가져온 이벤트들을 리스트에 저장
        List<Event> items = events.getItems();

        if (items.isEmpty()) {
            return "No Upcoming events found";
        } else {
            List<Map<String, Object>> list = new ArrayList<>();
            for (Event event : items) {
                if (event.getSummary() != null && event.getSummary().contains(location)) {

                    Map<String, Object> map = new HashMap<>();
                    map.put("summary", event.getSummary());
                    map.put("start", event.getStart().getDateTime().toString());
                    map.put("end", event.getEnd().getDateTime().toString());
                    map.put("eventid", event.getId());

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
                    if(localDate.isEqual(eventStartDate)) {
                        map.put("start_pixel",calPixel(event.getStart().getDateTime()));
                    }

                    //입력한 날짜보다 시작 날짜가 빠른 경우 -> start_pixel = 0
                    else if(localDate.isAfter(eventStartDate)) {
                        map.put("start_pixel","0");
                    }

                    //입력한 날짜와 이벤트 끝 날짜가 같은 경우 ->end pixel 그대로 계산
                    if(localDate.isEqual(eventEndDate)){
                        map.put("end_pixel",calPixel(event.getEnd().getDateTime()));
                    }

                    //입력한 날짜보다 이벤트 끝 날짜가 느린 경우 (입력 날짜에 이벤트가 끝나지 않은 경우) -> end pixel = 11:59 에 대하여 계산
                    else if(localDate.isBefore(eventEndDate)){
                        map.put("end_pixel",calPixel(timeAt1159));
                    }

                    list.add(map);
                }
            }

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = "";
            try {
                jsonString = objectMapper.writeValueAsString(list);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return jsonString;
        }
    }

    public String addEvent(String location, String student_id, String startDateTimeStr, String endDateTimeStr) throws Exception {
        //구글 캘린더 서비스에 접근할 수 있는 Calendar 객체 생성
        Calendar service = getCalendarService();

        DateTime startDateTime = new DateTime(startDateTimeStr);
        DateTime endDateTime = new DateTime(endDateTimeStr);

        // 기존 이벤트와의 충돌을 확인
        String date = startDateTime.toStringRfc3339().split("T")[0];
        String existingEventsJson = getCalendarEvents(date, location);

        // existingEventsJson로 겹치는 이벤트 있는지 확인
        if (!existingEventsJson.equals("No Upcoming events found")) {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> existingEvents = objectMapper.readValue(existingEventsJson, new TypeReference<List<Map<String, Object>>>(){});

            for (Map<String, Object> event : existingEvents) {
                DateTime existingStart = new DateTime((String) event.get("start"));
                DateTime existingEnd = new DateTime((String) event.get("end"));
                if (startDateTime.getValue() < existingEnd.getValue() && endDateTime.getValue() > existingStart.getValue()) {
                    // 겹치는 이벤트 발견하면 -> 로그 띄움
                    throw new Exception("An event at this location and time already exists.");
                }
            }
        }

        String summary = String.format("%s/%s", location, student_id);
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



    public String deleteCalendarEvents(String eventid) throws Exception {
        //구글 캘린더 서비스에 접근할 수 있는 Calendar 객체 생성
        Calendar service = getCalendarService();
        service.events().delete(CALENDAR_ID, eventid).execute();
        return "Event deleted successfully";
    }

    public String updateCalendarEvents(String location,String student_id, String startDateTimeStr, String endDateTimeStr, String eventid) throws Exception {
        //구글 캘린더 서비스에 접근할 수 있는 Calendar 객체 생성
        Calendar service = getCalendarService();

        Event event = service.events().get(CALENDAR_ID, eventid).execute();

        String summary = String.format("%s/%s",location,student_id);
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

        Event updatedEvent = service.events().update(CALENDAR_ID, eventid, event).execute();
        return updatedEvent.getId();
    }
}

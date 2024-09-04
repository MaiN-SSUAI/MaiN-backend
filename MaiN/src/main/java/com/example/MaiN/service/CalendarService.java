package com.example.MaiN.service;

import com.example.MaiN.security.GoogleCredentialProvider;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalendarService {
    private static final String APPLICATION_NAME = "Google Calendar API";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${calendar.id}")
    private String CALENDAR_ID;

    private final GoogleCredentialProvider googleCredentialProvider;
    public CalendarService(GoogleCredentialProvider googleCredentialProvider) {
        this.googleCredentialProvider = googleCredentialProvider;
    }

    @Operation(summary = "Calendar 서비스 객체 생성")
    private static Calendar getCalendarService() throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, GoogleCredentialProvider.getCredential())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    @Operation(summary = "예약 등록")
    public String addReservation(List studentIds, String startDateTimeStr, String endDateTimeStr) throws Exception {
        Calendar calendar = getCalendarService();
        DateTime startDateTime = new DateTime(startDateTimeStr);
        DateTime endDateTime = new DateTime(endDateTimeStr);

        String summary = String.format("세미나실2%s", studentIds);
        Event event = new Event().setSummary(summary);

        EventDateTime startEventDateTime = new EventDateTime().setDateTime(startDateTime).setTimeZone("Asia/Seoul");
        EventDateTime endEventDateTime = new EventDateTime().setDateTime(endDateTime).setTimeZone("Asia/Seoul");

        event.setStart(startEventDateTime);
        event.setEnd(endEventDateTime);
        event = calendar.events().insert(CALENDAR_ID, event).execute();
        return event.getId();
    }

    @Operation(summary = "예약 삭제")
    public void deleteReservation(String eventId) throws Exception {
        Calendar calendar = getCalendarService();
        calendar.events().delete(CALENDAR_ID, eventId).execute();
    }
}
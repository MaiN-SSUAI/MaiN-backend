package com.example.MaiN.service;

import com.example.MaiN.CalendarService.CalendarApproach;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalendarService {
    private static final String CALENDAR_ID = "d4075e67660e0f6bd313a60f05cbb102bc1b2a632c17c1a7e11acc1cf10fd8fe@group.calendar.google.com"; //학부

    public String addReservation(List studentIds, String startDateTimeStr, String endDateTimeStr) throws Exception{
        Calendar calendar = CalendarApproach.getCalendarService();
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
}
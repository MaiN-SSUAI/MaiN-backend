package com.example.MaiN.CalendarService;


import com.example.MaiN.entity.EventAssign;
import com.example.MaiN.entity.User;
import com.example.MaiN.repository.ReservAssignRepository;
import com.example.MaiN.repository.ReservRepository;
import com.example.MaiN.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.time.format.DateTimeFormatter;

@Service
public class CalendarService {
    private static final String CALENDAR_ID = "d4075e67660e0f6bd313a60f05cbb102bc1b2a632c17c1a7e11acc1cf10fd8fe@group.calendar.google.com"; //학부
    @Autowired
    private ReservRepository reservRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReservAssignRepository reservAssignRepository;
    @Autowired
    private CalendarValidService calendarValidService;
    // 예약 추가하기 (주최자)
    public void checkEvent(String studentId, String startDateTimeStr, String endDateTimeStr) throws Exception {
        DateTime startDateTime = new DateTime(startDateTimeStr);
        DateTime endDateTime = new DateTime(endDateTimeStr);
        LocalDate startDate = LocalDate.parse(startDateTimeStr.split("T")[0], DateTimeFormatter.ISO_DATE);
        // 에약 제한 사항들
        calendarValidService.checkEventOverlaps(startDateTime, endDateTime, startDate);
        calendarValidService.checkDuration(startDateTime, endDateTime);
    }

    public String addEvent(List studentIds, String startDateTimeStr, String endDateTimeStr) throws Exception{
        Calendar service = CalendarApproach.getCalendarService();
        DateTime startDateTime = new DateTime(startDateTimeStr);
        DateTime endDateTime = new DateTime(endDateTimeStr);

        String summary = String.format("세미나실2/%s", studentIds);
        Event event = new Event().setSummary(summary);

        EventDateTime start = new EventDateTime().setDateTime(startDateTime).setTimeZone("Asia/Seoul");
        EventDateTime end = new EventDateTime().setDateTime(endDateTime).setTimeZone("Asia/Seoul");

        event.setStart(start);
        event.setEnd(end);
        event = service.events().insert(CALENDAR_ID, event).execute();
        return event.getId();
    }

    public void addUninformedUser(String studentId) {
        User user = new User();
        user.setStudentNo(studentId);
        user.setStudentName("");
        user.setRefreshToken("");
        userRepository.save(user);
        System.out.println("Unauthorized User Addition Successful");
    }

    public void checkUser(String studentId, LocalDate date) {
        Optional<User> userOptional = userRepository.findByStudentNo(studentId);
        User user = userOptional.orElse(null);
        if(user == null){
            return;
        }
        int userId = user.getId();
        calendarValidService.checkEventsPerMonth(date);
        calendarValidService.checkEventsPerWeek(studentId, userId, date);
    }

    public void deleteCalendarEvents(String eventId) throws Exception {
        //구글 캘린더 서비스에 접근할 수 있는 Calendar 객체 생성
        Calendar service = CalendarApproach.getCalendarService();
        service.events().delete(CALENDAR_ID, eventId).execute();
    }

    public String updateCalendarEvents(String location,List<String> studentId, String startDateTimeStr, String endDateTimeStr, String eventId) throws Exception {
        //구글 캘린더 서비스에 접근할 수 있는 Calendar 객체 생성
        Calendar service = CalendarApproach.getCalendarService();

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


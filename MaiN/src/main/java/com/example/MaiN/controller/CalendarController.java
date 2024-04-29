package com.example.MaiN.controller;

import com.example.MaiN.dto.EventDto;
import com.example.MaiN.entity.Event;
import com.example.MaiN.service.CalendarService;
import com.example.MaiN.repository.ReservRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping(value = "/calendar")
public class CalendarController {
    @Autowired
    private final ReservRepository reservRepository;

    @Autowired
    private CalendarService calendarService;

    public CalendarController(ReservRepository seminarReservRepository) {
        reservRepository = seminarReservRepository;
    }

    //특정 날짜 일정 보기
    @GetMapping("/show_event")
    public String getCalendarEvents(@RequestParam(name="date") String date, @RequestParam(name="location") String location) throws Exception {
        return calendarService.getCalendarEvents(date,location);
    }
    //일정 추가
    @PostMapping("/add")
    public String addEvent(@RequestBody EventDto eventDto) throws IOException, GeneralSecurityException, Exception {
        String eventId = calendarService.addEvent(eventDto.getLocation(), eventDto.getStudentId(), eventDto.getStartDateTimeStr(), eventDto.getEndDateTimeStr());
        eventDto.setEventId(eventId);
        Event event = eventDto.toEntity(); //dto를 entity로 변환
        Event saved = reservRepository.save(event); //repository를 이용하여 entity를 db에 저장
        return "success";
    }

    //삭제
    @DeleteMapping("/delete/{eventId}")
    public String delete(@PathVariable("eventId") String eventId) throws Exception {
        Event target = reservRepository.findByEventId(eventId);

        reservRepository.delete(target);
        return calendarService.deleteCalendarEvents(eventId);
    }

    //수정
    @PatchMapping("/patch/{eventId}")
    public String patch(@PathVariable("eventId") String eventId, @RequestBody EventDto eventDto) throws Exception{

        Event event = eventDto.toEntity(); //DTO->entity 변환
        Event target = reservRepository.findByEventId(eventId); //타깃 조회
        target.patch(event);
        Event updated = reservRepository.save(target);
        return calendarService.updateCalendarEvents(eventDto.getLocation(), eventDto.getStudentId(), eventDto.getStartDateTimeStr(), eventDto.getEndDateTimeStr(),eventId);

    }

}

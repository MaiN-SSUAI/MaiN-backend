package com.example.MaiN.controller;

import com.example.MaiN.dto.EventDto;
import com.example.MaiN.entity.EventEntity;
import com.example.MaiN.service.CalendarService;
import com.example.MaiN.repository.seminar_reserv_Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping(value = "/calendar")
public class CalendarController {
    @Autowired
    private final seminar_reserv_Repository seminar_reserv_Repository;

    @Autowired
    private CalendarService calendarService;

    public CalendarController(com.example.MaiN.repository.seminar_reserv_Repository seminarReservRepository) {
        seminar_reserv_Repository = seminarReservRepository;
    }

    //특정 날짜 일정 보기
    @GetMapping("/show_event")
    public String getCalendarEvents(@RequestParam(name="date") String date, @RequestParam(name="location") String location) throws Exception {
        return calendarService.getCalendarEvents(date,location);
    }
    //일정 추가
    @PostMapping("/add")
    public String addEvent(@RequestBody EventDto eventDto) throws IOException, GeneralSecurityException, Exception {
        String eventid = calendarService.addEvent(eventDto.getLocation(), eventDto.getStudent_id(), eventDto.getStartDateTimeStr(), eventDto.getEndDateTimeStr());
        eventDto.setEventId(eventid);
        EventEntity eventEntity = eventDto.toEntity(); //dto를 entity로 변환
        EventEntity saved = seminar_reserv_Repository.save(eventEntity); //repository를 이용하여 entity를 db에 저장
        return "";
    }

    //삭제
    @DeleteMapping("/delete/{eventid}")
    public String delete(@PathVariable("eventid") String eventid) throws Exception {
        EventEntity target = seminar_reserv_Repository.findByeventid(eventid);

        seminar_reserv_Repository.delete(target);
        return calendarService.deleteCalendarEvents(eventid);
    }

    //수정
    @PatchMapping("/patch/{eventid}")
    public String patch(@PathVariable("eventid") String eventid, @RequestBody EventDto eventDto) throws Exception{

        EventEntity eventEntity = eventDto.toEntity(); //DTO->entity 변환
        EventEntity target = seminar_reserv_Repository.findByeventid(eventid); //타깃 조회
        target.patch(eventEntity);
        EventEntity updated = seminar_reserv_Repository.save(target);
        return calendarService.updateCalendarEvents(eventDto.getLocation(), eventDto.getStudent_id(), eventDto.getStartDateTimeStr(), eventDto.getEndDateTimeStr(),eventid);

    }

}

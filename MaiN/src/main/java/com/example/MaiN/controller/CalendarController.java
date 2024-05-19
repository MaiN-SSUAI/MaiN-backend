package com.example.MaiN.controller;

import com.example.MaiN.dto.EventDto;
import com.example.MaiN.dto.UserDto;
import com.example.MaiN.entity.Event;
import com.example.MaiN.service.CalendarService;
import com.example.MaiN.repository.ReservRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.List;
import java.util.Collections;
import java.util.Map;

@RestController
@Tag(name="Calendar-Controller",description = "세미나실 예약 관련 API")
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
    @GetMapping("/events")
    @Operation(summary = "모든 예약 불러오기")
    public ResponseEntity<?> getCalendarEvents(@RequestParam(name="date") LocalDate date, @RequestParam(name="location") String location) throws Exception {
        return calendarService.getCalendarEvents(date,location);
    }

    @GetMapping("/check/user")
    @Operation(summary = "세미나실 사용자 등록")
    public ResponseEntity<?> addUsers(@RequestBody UserDto userDto, @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        return calendarService.checkUser(userDto, date);
    }
    //일정 추가
    @PostMapping("/add/event")
    @Operation(summary = "예약 등록")
    public String addEvent(@RequestBody EventDto eventDto) throws IOException, GeneralSecurityException, Exception {
        List<String> studentIds = eventDto.getStudentIds();
        for (int i = 0; i<studentIds.size(); i++) {
            String studentId = studentIds.get(i);
            if (i == 0) {
                String eventId = calendarService.addOrganizeEvent(eventDto.getLocation(), studentId, eventDto.getStartDateTimeStr(), eventDto.getEndDateTimeStr(), eventDto.getPurpose());
                eventDto.setEventId(eventId);
                Event event = eventDto.toEntity(studentId);
                Event saved = reservRepository.save(event);
            } else {
                String eventId = calendarService.addEvent(eventDto.getLocation(), studentId, eventDto.getStartDateTimeStr(), eventDto.getEndDateTimeStr(), eventDto.getPurpose());
                eventDto.setEventId(eventId);
                Event event = eventDto.toEntity(studentId);
                Event saved = reservRepository.save(event);
            }
        }
        return "success";
    }

    //삭제
    @DeleteMapping("/delete/{eventId}")
    @Operation(summary = "예약 삭제")
    public String delete(@PathVariable("eventId") String eventId) throws Exception {
        Event target = reservRepository.findByEventId(eventId);

        reservRepository.delete(target);
        return calendarService.deleteCalendarEvents(eventId);
    }

    //수정
    @PatchMapping("/patch/{eventId}")
    @Operation(summary = "예약 수정")
    public String patch(@PathVariable("eventId") String eventId, @RequestBody EventDto eventDto) throws Exception{

        Event event = eventDto.toEntity(eventId); //DTO->entity 변환
        Event target = reservRepository.findByEventId(eventId); //타깃 조회
        target.patch(event);
        Event updated = reservRepository.save(target);
        return calendarService.updateCalendarEvents(eventDto.getLocation(), eventDto.getStudentIds(), eventDto.getStartDateTimeStr(), eventDto.getEndDateTimeStr(),eventId);

    }

}
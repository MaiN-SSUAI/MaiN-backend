package com.example.MaiN.controller;

import com.example.MaiN.dto.EventAssignDto;
import com.example.MaiN.dto.EventDto;
import com.example.MaiN.dto.UserDto;
import com.example.MaiN.entity.Event;
import com.example.MaiN.entity.EventAssign;
import com.example.MaiN.entity.User;
import com.example.MaiN.repository.ReservAssignRepository;
import com.example.MaiN.repository.UserRepository;
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
import java.util.Optional;

@RestController
@Tag(name="Calendar-Controller",description = "세미나실 예약 관련 API")
@RequestMapping(value = "/calendar")
public class CalendarController {
    @Autowired
    private final ReservRepository reservRepository;
    @Autowired
    private ReservAssignRepository reservAssignRepository;
    @Autowired
    private CalendarService calendarService;
    @Autowired
    private UserRepository userRepository;

    public CalendarController(ReservRepository seminarReservRepository) {
        reservRepository = seminarReservRepository;
    }

    //특정 날짜 일정 보기
    @GetMapping("/events")
    @Operation(summary = "모든 예약 불러오기")
    public ResponseEntity<?> getCalendarEvents(@RequestParam(name="date") LocalDate date, @RequestParam(name="location") String location) throws Exception {
        return calendarService.getCalendarEvents(date);
    }

    @GetMapping("/check/user")
    @Operation(summary = "세미나실 사용자 등록")
    public ResponseEntity<?> addUsers(@RequestBody UserDto UserDto, @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        return calendarService.checkUser(UserDto, date);
    }
    @PostMapping("/add/event")
    @Operation(summary = "예약 등록")
    public String addEvent(@RequestBody EventDto eventDto) throws IOException, GeneralSecurityException, Exception {
        List<String> studentIds = eventDto.getStudentIds();
        int reservId = 0;
        for (int i = 0; i<studentIds.size(); i++) {
            String studentId = studentIds.get(i);
            User user = userRepository.findByStudentNo(studentId);
            int userId = user.getId();
            if (i == 0) {
                String eventId = calendarService.addOrganizeEvent(studentId, userId, eventDto.getPurpose(), eventDto.getStartDateTimeStr(), eventDto.getEndDateTimeStr());
                Event event = eventDto.toEntity(userId);
                Event saved = reservRepository.save(event);
                reservId = saved.getId();
                EventAssignDto eventAssignDto = new EventAssignDto(reservId, userId, eventId);
                EventAssign eventOther = eventAssignDto.toEntity();
                reservAssignRepository.save(eventOther);
            }
            else {
                String eventId = calendarService.addEvent(studentId, userId, eventDto.getPurpose(), eventDto.getStartDateTimeStr(), eventDto.getEndDateTimeStr());
                EventAssignDto eventAssignDto = new EventAssignDto(reservId, userId, eventId);
                EventAssign eventOther = eventAssignDto.toEntity();
                reservAssignRepository.save(eventOther);
            }

        }
        return "success";
    }

    //삭제
    @DeleteMapping("/delete/{Id}")
    @Operation(summary = "예약 삭제")
    public String delete(@PathVariable("Id") int id) throws Exception {
        List<EventAssign> eventAssignList = reservAssignRepository.findByReservId(id);
        if (!eventAssignList.isEmpty()) {
            for (EventAssign eventAssign : eventAssignList) {
                String eventId = eventAssign.getEventId();
                reservAssignRepository.delete(eventAssign);
                calendarService.deleteCalendarEvents(eventId);
            }
            Optional<Event> target = reservRepository.findById(id);
            if (target.isPresent()) {
                Event event = target.get();
                reservRepository.delete(event);
            }
            return "Events deleted successfully";
        } else {
            throw new Exception("EventAssign not found for id: " + id);
        }
    }

    //수정
    /*@PatchMapping("/patch/{eventId}")
    @Operation(summary = "예약 수정")
    public String patch(@PathVariable("eventId") String eventId, @RequestBody EventDto eventDto) throws Exception{

        Event event = eventDto.toEntity(eventId); //DTO->entity 변환
        Event target = reservRepository.findByEventId(eventId); //타깃 조회
        target.patch(event);
        Event updated = reservRepository.save(target);
        return calendarService.updateCalendarEvents(eventDto.getStudentIds(), eventDto.getStartDateTimeStr(), eventDto.getEndDateTimeStr(),eventId);
    }*/

}
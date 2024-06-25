package com.example.MaiN.controller;

import com.example.MaiN.CalendarService.CalendarGetService;
import com.example.MaiN.CalendarService.CalendarValidService;
import com.example.MaiN.Exception.CustomErrorCode;
import com.example.MaiN.Exception.CustomException;
import com.example.MaiN.dto.EventAssignDto;
import com.example.MaiN.dto.EventDto;
import com.example.MaiN.entity.Event;
import com.example.MaiN.entity.EventAssign;
import com.example.MaiN.entity.User;
import com.example.MaiN.repository.ReservAssignRepository;
import com.example.MaiN.repository.UserRepository;
import com.example.MaiN.CalendarService.CalendarService;
import com.example.MaiN.repository.ReservRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
    private CalendarGetService calendarGetService;
    @Autowired
    private CalendarValidService calendarValidService;
    @Autowired
    private UserRepository userRepository;

    private static final String CALENDAR_ID = "d4075e67660e0f6bd313a60f05cbb102bc1b2a632c17c1a7e11acc1cf10fd8fe@group.calendar.google.com";

    public CalendarController(ReservRepository seminarReservRepository) {
        reservRepository = seminarReservRepository;
    }
    //특정 날짜 일정 보기
    @GetMapping("/events")
    @Operation(summary = "모든 예약 불러오기")
    public ResponseEntity<?> getCalendarEvents(@RequestParam(name="date") LocalDate date) throws Exception {
        return calendarGetService.getCalendarEvents(date);
    }

    @GetMapping("/events/week")
    public ResponseEntity<?> getWeekCalendarEvents(@RequestParam("date") String startDateStr) {
        try {
            LocalDate date = LocalDate.parse(startDateStr);
            return calendarGetService.getWeekCalendarEvents(date);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid Input Value.");
        }
    }

    @GetMapping("/check/user")
    @Operation(summary = "세미나실 사용자 등록")
    public ResponseEntity<?> addUsers(@RequestParam("user") String user, @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        calendarService.checkUser(user, date);
        return ResponseEntity.ok("Valid User");
    }
    @PostMapping("/add/event")
    @Operation(summary = "예약 등록")
    public String addEvent(@RequestBody EventDto eventDto) throws Exception {
        if (eventDto.getStudentIds().size() < 2){
            throw new CustomException("최소 2인 이상 예약해야 합니다.", CustomErrorCode.RESERVATION_ONE_PERSON);
        }
        List<String> studentIds = eventDto.getStudentIds();
        int reservId = 0;
        for (int i = 0; i<studentIds.size(); i++) {
            if (i == 0) {
                String studentId = studentIds.get(i);
                Optional<User> userOptional = userRepository.findByStudentNo(studentId);
                User user = userOptional.orElse(null);
                int userId = user.getId();
                String eventId = calendarService.addOrganizeEvent(studentId, eventDto.getStartDateTimeStr(), eventDto.getEndDateTimeStr());
                Event event = eventDto.toEntity(userId);
                Event saved = reservRepository.save(event); //대표 이벤트 저장
                reservId = saved.getId();
                EventAssignDto eventAssignDto = new EventAssignDto(reservId, userId, eventId);
                EventAssign eventOther = eventAssignDto.toEntity(); //대표 이벤트를 나머지 이벤트에 한번 더 저장 (삭제용)
                reservAssignRepository.save(eventOther);
            }
            else {
                int userId;
                String studentId = studentIds.get(i);
                Optional<User> userOptional = userRepository.findByStudentNo(studentId);
                User user = userOptional.orElse(null);

                if(user == null){
                    userId = 1;
                    calendarService.addUninformedUser(studentId);
                } //정보 없는 학생일 경우 userId = 1
                else{ userId = user.getId(); } //정보 있는 학생일 경우 정보 가져옴

                String eventId = calendarService.addEvent(studentId, eventDto.getStartDateTimeStr(), eventDto.getEndDateTimeStr());

                EventAssignDto eventAssignDto = new EventAssignDto(reservId, userId, eventId); //나머지 이벤트 저장
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
        calendarValidService.checkDeleteTime(id);

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
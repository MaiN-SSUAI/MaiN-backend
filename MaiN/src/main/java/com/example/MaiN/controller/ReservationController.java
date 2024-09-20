package com.example.MaiN.controller;

import com.example.MaiN.entity.Reserv;
import com.example.MaiN.repository.ReservRepository;
import com.example.MaiN.service.CalendarService;
import com.example.MaiN.service.ReservationService;
import com.example.MaiN.dto.EventDto;
import com.google.api.client.util.DateTime;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequiredArgsConstructor
@Tag(name="Calendar-Controller",description = "세미나실 예약 관련 API")
@RequestMapping(value = "/calendar")
public class ReservationController {

    private final ReservationService reservationService;
    private final CalendarService calendarService;
    private final ReservRepository reservRepository;

    @GetMapping("/events")
    @Operation(summary = "세미나실 예약 조회 (DAY)")
    public ResponseEntity<?> getDayEvents(@RequestParam("date")LocalDate date) throws Exception {
        return ResponseEntity.ok(calendarService.getDayCalendarReservations(date));
    }

    @GetMapping("/events/week")
    @Operation(summary = "세미나실 예약 조회 (WEEK)")
    public ResponseEntity<?> getWeekEvents(@RequestParam("date")LocalDate date) throws Exception {
//        return ResponseEntity.ok(calendarService.getWeekCalendarReservations(date));
        return ResponseEntity.ok(calendarService.getWeeklyCalendarReservations(date));
    }

    @GetMapping("/events/month")
    @Operation(summary = "세미나실 예약 조회 (MONTH) ")
    public ResponseEntity<?> getMonthEvents(@RequestParam("date")LocalDate date) throws Exception {
        return ResponseEntity.ok(calendarService.getMonthCalendarReservations(date));
    }

    @GetMapping("/check/user")
    @Operation(summary = "세미나실 사용자 등록")
    public ResponseEntity<?> addUsers(@RequestParam("user") String user, @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        reservationService.checkUser(user, date);
        return ResponseEntity.ok("Valid User");
    }

    // 예약 등록
    @PostMapping("/add/event")
    @Operation(summary = "예약 등록")
    public ResponseEntity<?> addReservation(@RequestBody EventDto eventDto) throws Exception {
        return ResponseEntity.ok(reservationService.addReservation(eventDto));
    }

    // 예약 삭제
    @DeleteMapping("/delete/{reservId}")
    @Operation(summary = "예약 삭제")
    public ResponseEntity<String> deleteReservation(@PathVariable("reservId") int reservId) throws Exception {
        return ResponseEntity.ok(reservationService.deleteReservation(reservId));
    }

    @PatchMapping("/patch/{reservId}")
    @Operation(summary = "예약 수정")
    public ResponseEntity<String> updateReservation(@PathVariable("reservId") int reservId, @RequestBody EventDto eventDto) throws Exception {
        return ResponseEntity.ok(reservationService.updateReservation(reservId,eventDto));
    }

    // 수정 개발자 테스트
    @PatchMapping("/test/patch/{reservId}")
    public ResponseEntity<DateTime> testUpdateReservation(@PathVariable("reservId") int reservId, @RequestBody EventDto eventDto) throws Exception {
        Reserv reserv = reservRepository.findByReservId(reservId);
        return ResponseEntity.ok(calendarService.updateReservation(reserv.getEventId(), eventDto.getStudentIds(), eventDto.getStartDateTime(), eventDto.getEndDateTime()));
    }
}
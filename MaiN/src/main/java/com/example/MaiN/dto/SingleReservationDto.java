package com.example.MaiN.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SingleReservationDto {
    private int reservationId;
    private List<String> studentNo;
    private String purpose;
    private LocalDateTime start;
    private LocalDateTime end;
    private String start_pixel;
    private String end_pixel;
}

package com.example.MaiN.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class SingleReservationDto {
    private int reservationId;
    private List<String> studentNo;
    private String purpose;
    private String start;
    private String end;
    private String start_pixel;
    private String end_pixel;
}

package com.example.MaiN.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class DayReservationResponse {
    private List<SingleReservationDto> reservations;

    public DayReservationResponse() {
        this.reservations = new ArrayList<>();;
    }

    // 인자를 받는 생성자 (필요한 경우)
    public DayReservationResponse(List<SingleReservationDto> reservations) {
        this.reservations = reservations;
    }

    public void addSingleReservation(SingleReservationDto singleReservationDto) {
        this.reservations.add(singleReservationDto);
    }
}

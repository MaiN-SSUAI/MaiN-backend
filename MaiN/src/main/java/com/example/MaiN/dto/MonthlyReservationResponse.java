package com.example.MaiN.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({ "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" })
public class MonthlyReservationResponse {
    private WeeklyReservationResponse week1 = new WeeklyReservationResponse();
    private WeeklyReservationResponse week2 = new WeeklyReservationResponse();
    private WeeklyReservationResponse week3 = new WeeklyReservationResponse();
    private WeeklyReservationResponse week4 = new WeeklyReservationResponse();
}

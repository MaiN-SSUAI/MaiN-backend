package com.example.MaiN.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonPropertyOrder({ "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" })
public class WeeklyReservationResponse {
    private DayReservationResponse Mon = new DayReservationResponse();
    private DayReservationResponse Tue = new DayReservationResponse();
    private DayReservationResponse Wed = new DayReservationResponse();
    private DayReservationResponse Thu = new DayReservationResponse();
    private DayReservationResponse Fri = new DayReservationResponse();
    private DayReservationResponse Sat = new DayReservationResponse();
    private DayReservationResponse Sun = new DayReservationResponse();


    public void addReservationList(String day, DayReservationResponse reservation) {
        switch (day) {
            case "Mon":
                this.Mon.getReservations().addAll(reservation.getReservations());
                break;
            case "Tue":
                this.Tue.getReservations().addAll(reservation.getReservations());
                break;
            case "Wed":
                this.Wed.getReservations().addAll(reservation.getReservations());
                break;
            case "Thu":
                this.Thu.getReservations().addAll(reservation.getReservations());
                break;
            case "Fri":
                this.Fri.getReservations().addAll(reservation.getReservations());
                break;
            case "Sat":
                this.Sat.getReservations().addAll(reservation.getReservations());
                break;
            case "Sun":
                this.Sun.getReservations().addAll(reservation.getReservations());
                break;
        }
    }

}

package com.example.MaiN.dto;

import com.example.MaiN.entity.Reserv;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EventDto {
    private int id;
    private String eventId;
    private int userId;
    private List<String> studentIds;
    private String purpose;
    private String startDateTimeStr;
    private String endDateTimeStr;
    private int reservationId;

    // DTO객체를 Entity객체로 변환하는 메서드
    public Reserv toEntity(int userId){
        return new Reserv(
                this.id,
                userId,
                this.purpose,
                this.startDateTimeStr,
                this.endDateTimeStr,
                this.eventId
        );
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventId() {
        return eventId;
    }
}

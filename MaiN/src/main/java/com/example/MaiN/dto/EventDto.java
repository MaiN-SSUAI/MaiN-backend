package com.example.MaiN.dto;

import com.example.MaiN.entity.Event;
import com.example.MaiN.entity.EventAssign;
import com.google.api.client.util.DateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import java.time.OffsetDateTime;

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
    public Event toEntity(int userId){
        return new Event(
                this.id,
                userId,
                this.purpose,
                this.startDateTimeStr,
                this.endDateTimeStr
        );
    }
}

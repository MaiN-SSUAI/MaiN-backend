package com.example.MaiN.dto;

import com.example.MaiN.entity.Event;
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
    private String location;
    private List<String> studentIds;
    private String startDateTimeStr;
    private String endDateTimeStr;
    @Setter
    private String eventId;

    // DTO객체를 Entity객체로 변환하는 메서드
    public Event toEntity(String studentId){
        return new Event(
                this.location,
                studentId,
                this.startDateTimeStr,
                this.endDateTimeStr,
                this.eventId
        );
    }
}
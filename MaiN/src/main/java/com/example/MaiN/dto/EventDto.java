package com.example.MaiN.dto;

import com.example.MaiN.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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


    //DTO객체를 Entity객체로 변환하는 메서드
    public Event toEntity(){
        return Event.builder()
                .location(location)
                .studentIds(studentIds)
                .startTime(startDateTimeStr)
                .endTime(endDateTimeStr)
                .eventId(eventId)
                .build();
    }

}

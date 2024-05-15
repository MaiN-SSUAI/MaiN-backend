package com.example.MaiN.dto;

import com.example.MaiN.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EventDto {
    private String location;
    private String studentNo;
    private String startDateTimeStr;
    private String endDateTimeStr;
    @Setter
    private String eventId;


    //DTO객체를 Entity객체로 변환하는 메서드
    public Event toEntity(){
        return Event.builder()
                .location(location)
                .studentNo(studentNo)
                .startTime(startDateTimeStr)
                .endTime(endDateTimeStr)
                .eventId(eventId)
                .build();
    }

}

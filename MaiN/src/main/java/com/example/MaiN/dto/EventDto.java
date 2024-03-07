package com.example.MaiN.dto;

import com.example.MaiN.entity.EventEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EventDto {
    private String location;
    private String student_id;
    private String startDateTimeStr;
    private String endDateTimeStr;

    @Setter
    private String eventId;


    //DTO객체를 Entity객체로 변환하는 메서드
    public EventEntity toEntity(){
        return EventEntity.builder()
                .location(location)
                .student_id(student_id)
                .start_time(startDateTimeStr)
                .end_time(endDateTimeStr)
                .eventid(eventId)
                .build();
    }

}

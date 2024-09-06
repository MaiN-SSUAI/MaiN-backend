package com.example.MaiN.entity;

import com.example.MaiN.dto.EventDto;
import com.google.api.client.util.DateTime;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import java.time.OffsetDateTime;

@NoArgsConstructor
@Entity
@Setter
@Getter
@Builder
@Table(name="reserv")
public class Reserv {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id")
    private int userId;

    @Column (name = "purpose")
    private String purpose;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "end_time")
    private String endTime;

    @Column(name = "event_id")
    private String eventId;


    public Reserv(int id, int userId, String purpose, String startTime, String endTime, String eventId) {
        this.id = id;
        this.userId = userId;
        this.purpose = purpose;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventId = eventId;
    }

    public Reserv(EventDto eventDto){
        this.id = eventDto.getId();
        this.userId = eventDto.getUserId();
        this.purpose = eventDto.getPurpose();
        this.startTime = eventDto.getStartDateTimeStr();
        this.endTime = eventDto.getEndDateTimeStr();
        this.eventId = eventDto.getEventId();
    }

    public void updateReserv(EventDto eventDto){
        if(!eventDto.getPurpose().isEmpty()) this.purpose = eventDto.getPurpose();
        if(!eventDto.getStartDateTimeStr().isEmpty()) this.startTime = eventDto.getStartDateTimeStr();
        if(!eventDto.getEndDateTimeStr().isEmpty()) this.endTime = eventDto.getEndDateTimeStr();
    }


    public String getEventId() {
        return eventId;
    }
}

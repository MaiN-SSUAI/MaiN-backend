package com.example.MaiN.entity;

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

    public void patch(Reserv event) {
        if(event.purpose != null)
            this.purpose = event.purpose;
        if (event.startTime != null)
            this.startTime = event.startTime;
        if (event.endTime != null)
            this.endTime = event.endTime;

    }

    public String getEventId() {
        return eventId;
    }
}

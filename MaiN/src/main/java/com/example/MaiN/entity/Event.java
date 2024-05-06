package com.example.MaiN.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Setter
@Getter
@Builder
@Table(name="seminar_reserv")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String location;

    @Column(name = "student_ids")
    private List<String> studentIds;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "end_time")
    private String endTime;

    @Column(name = "event_id")
    private String eventId;

    public void patch(Event event) {
        if (event.location != null)
            this.location = event.location;
        if(event.studentIds != null)
            this.studentIds = event.studentIds;
        if (event.startTime != null)
            this.startTime = event.startTime;
        if (event.endTime != null)
            this.endTime = event.endTime;
        if (event.eventId != null)
            this.eventId = event.eventId;
    }



}

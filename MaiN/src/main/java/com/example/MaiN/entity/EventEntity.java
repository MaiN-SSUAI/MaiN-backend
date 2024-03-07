package com.example.MaiN.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Setter
@Getter
@Builder
@Table(name="seminar_reserv")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String location;

    @Column
    private String student_id;

    @Column
    private String start_time;

    @Column
    private String end_time;

    @Column
    private String eventid;

    public void patch(EventEntity eventEntity) {
        if (eventEntity.location != null)
            this.location = eventEntity.location;
        if(eventEntity.student_id != null)
            this.student_id = eventEntity.student_id;
        if (eventEntity.start_time != null)
            this.start_time = eventEntity.start_time;
        if (eventEntity.end_time != null)
            this.end_time = eventEntity.end_time;
        if (eventEntity.eventid != null)
            this.eventid = eventEntity.eventid;
    }

}

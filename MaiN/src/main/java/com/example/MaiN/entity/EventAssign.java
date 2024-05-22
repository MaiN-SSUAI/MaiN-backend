package com.example.MaiN.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Setter
@Getter
@Table(name="reserv_assign")
public class EventAssign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "reservation_id")
    private int reservId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "event_id")
    private String eventId;

    public EventAssign(int reservId, int userId, String eventId) {
        this.reservId = reservId;
        this.userId = userId;
        this.eventId = eventId;
    }
}

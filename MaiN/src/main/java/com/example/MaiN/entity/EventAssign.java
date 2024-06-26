package com.example.MaiN.entity;

import jakarta.persistence.*;
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

    public EventAssign(int reservId, int userId) {
        this.reservId = reservId;
        this.userId = userId;
    }
}

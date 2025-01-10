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
public class ReservAssign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "reservation_id")
    private Reserv reserv;

    @Column(name = "user_id")
    private int userId;

    public ReservAssign(Reserv reserv, int userId) {
        this.reserv = reserv;
        this.userId = userId;
    }

    public Integer getReservId() {
        return reserv.getId();
    }
}

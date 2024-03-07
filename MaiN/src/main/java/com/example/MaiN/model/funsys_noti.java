package com.example.MaiN.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Getter
@NoArgsConstructor
@Entity
@Table(name="funsys_noti")
public class funsys_noti {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "title", unique = true, nullable = false)
    private String title;

    @Column(length = 15, nullable = false)
    private String link;

    @Column(name = "start_date",nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate end_date;

    @Column(nullable = false)
    private boolean favorites;

    @OneToMany(mappedBy = "funsysNoti", fetch = FetchType.EAGER)
    private Set<funsys_noti_favorites> favoritesSet;

    @Builder
    public funsys_noti(int id, String title, String link, LocalDate start_date, LocalDate end_date, boolean favorites) {
        this.id = Math.toIntExact(id);
        this.title = title;
        this.link = link;
        this.startDate = startDate;
        this.end_date = end_date;
        this.favorites = favorites;
    }
}

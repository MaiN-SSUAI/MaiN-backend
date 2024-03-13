package com.example.MaiN.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Getter
@NoArgsConstructor
@Entity
@Table(name="ai_noti")
public class ai_noti {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "title", unique = true, nullable = false)
    private String title;

    @Column(length = 15, nullable = false)
    private String link;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private boolean favorites;

    @OneToMany(mappedBy = "aiNoti", fetch = FetchType.EAGER)
    private Set<ai_noti_favorites> favoritesSet;

    @Builder
    public ai_noti(int id, String title, String link, LocalDate date, boolean favorites) {
        this.id = Math.toIntExact(id);
        this.title = title;
        this.link = link;
        this.date = date;
        this.favorites = favorites;
    }
}

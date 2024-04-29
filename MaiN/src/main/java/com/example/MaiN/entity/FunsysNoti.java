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
@Table(name="funsys_noti")
public class FunsysNoti {
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
    private LocalDate endDate;

    @OneToMany(mappedBy = "funsysNoti", fetch = FetchType.EAGER)
    private Set<FunsysNotiFavor> favoritesSet;

    @Builder
    public FunsysNoti(int id, String title, String link, LocalDate startDate, LocalDate endDate) {
        this.id = Math.toIntExact(id);
        this.title = title;
        this.link = link;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}

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
@Table(name="ssucatch_noti")
public class SsucatchNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title", unique = true, nullable = false)
    private String title;

    @Column(length = 15, nullable = false)
    private String link;

    @Column(nullable = false)
    private String progress;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private LocalDate date;

    @OneToMany(mappedBy = "ssuCatchNoti", fetch = FetchType.EAGER)
    private Set<SsuCatchNotiFavor> favoritesSet;

    @Builder
    public SsucatchNotice(int id, String title, String link, String progress, String category, LocalDate date) {
        this.id = Math.toIntExact(id);
        this.title = title;
        this.link = link;
        this.progress = progress;
        this.category = category;
        this.date = date;
    }
    public boolean isFavorites() {
        return favoritesSet != null && !favoritesSet.isEmpty();
    }
}

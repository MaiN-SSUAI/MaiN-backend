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
public class AiNotice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title", unique = true, nullable = false)
    private String title;

    @Column(length = 15, nullable = false)
    private String link;

    @Column(nullable = false)
    private LocalDate date;

    @OneToMany(mappedBy = "noticeId", fetch = FetchType.EAGER)
    private Set<NoticeFavorite> favoritesSet;

    @Builder
    public AiNotice(int id, String title, String link, LocalDate date) {
        this.id = Math.toIntExact(id);
        this.title = title;
        this.link = link;
        this.date = date;
    }

    public boolean isFavorites() {
        return favoritesSet != null && !favoritesSet.isEmpty();
    }
}

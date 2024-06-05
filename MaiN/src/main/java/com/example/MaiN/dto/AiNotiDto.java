package com.example.MaiN.dto;

import com.example.MaiN.entity.AiNoti;

import java.time.LocalDate;

public class AiNotiDto {
    private Integer id;
    private String title;
    private String link;
    private LocalDate date;
    private boolean favorites;
    public AiNotiDto(int id, String title, String link, LocalDate date, boolean favorites) {
        this.id = Math.toIntExact(id);
        this.title = title;
        this.link = link;
        this.date = date;
        this.favorites = favorites;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean getFavorites() {
        return favorites;
    }
    // 추가된 from 메서드
    public static AiNotiDto from(AiNoti aiNoti) {
        return new AiNotiDto(
                Math.toIntExact(aiNoti.getId()),
                aiNoti.getTitle(),
                aiNoti.getLink(),
                aiNoti.getDate(),
                aiNoti.isFavorites()
        );
    }
}

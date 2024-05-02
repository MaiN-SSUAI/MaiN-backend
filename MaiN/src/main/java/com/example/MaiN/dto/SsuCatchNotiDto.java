package com.example.MaiN.dto;

import java.time.LocalDate;

public class SsuCatchNotiDto {
    private int id;
    private String title;
    private String link;
    private String progress;
    private String category;
    private LocalDate date;
    private boolean favorites;
    public SsuCatchNotiDto(int id, String title, String link, String progress, String category, LocalDate date, boolean favorites) {
        this.id = Math.toIntExact(id);
        this.title = title;
        this.link = link;
        this.progress = progress;
        this.category = category;
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
    public String getProgress() {
        return progress;
    }
    public String getCategory() {
        return category;
    }
    public LocalDate getsDate() { return date; }
    public boolean getFavorites() {
        return favorites;
    }
}

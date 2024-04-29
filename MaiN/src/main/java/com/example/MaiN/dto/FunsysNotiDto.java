package com.example.MaiN.dto;

import java.time.LocalDate;

public class FunsysNotiDto {
    private int id;
    private String title;
    private String link;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean favorites;
    public FunsysNotiDto(int id, String title, String link, LocalDate startDate, LocalDate endDate, boolean favorites) {
        this.id = Math.toIntExact(id);
        this.title = title;
        this.link = link;
        this.startDate = startDate;
        this.endDate = endDate;
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
    public LocalDate getstartDate() { return startDate; }
    public LocalDate getendDate() { return endDate; }
    public boolean getFavorites() {
        return favorites;
    }
}

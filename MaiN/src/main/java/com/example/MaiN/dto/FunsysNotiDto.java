package com.example.MaiN.dto;

import com.example.MaiN.entity.AiNoti;
import com.example.MaiN.entity.FunsysNoti;

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
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public boolean getFavorites() {
        return favorites;
    }

    public static FunsysNotiDto from(FunsysNoti funsysNoti) {
        return new FunsysNotiDto(
                Math.toIntExact(funsysNoti.getId()),
                funsysNoti.getTitle(),
                funsysNoti.getLink(),
                funsysNoti.getStartDate(),
                funsysNoti.getEndDate(),
                funsysNoti.isFavorites()
        );
    }
}

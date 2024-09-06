package com.example.MaiN.dto;

import com.example.MaiN.entity.SsucatchNotice;
import lombok.Getter;

import java.time.LocalDate;


@Getter
public class SsucatchNoticeDto {

    private final int id;
    private final String title;
    private final String link;
    private final String progress;
    private final String category;
    private final LocalDate date;

    public SsucatchNoticeDto(int id, String title, String link, String progress, String category, LocalDate date) {
        this.id = Math.toIntExact(id);
        this.title = title;
        this.link = link;
        this.progress = progress;
        this.category = category;
        this.date = date;
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
    public LocalDate getDate() { return date; }

    public static SsucatchNoticeDto from(SsucatchNotice ssucatchNotice) {
        return new SsucatchNoticeDto(
                Math.toIntExact(ssucatchNotice.getId()),
                ssucatchNotice.getTitle(),
                ssucatchNotice.getLink(),
                ssucatchNotice.getProgress(),
                ssucatchNotice.getCategory(),
                ssucatchNotice.getDate()
        );
    }
}

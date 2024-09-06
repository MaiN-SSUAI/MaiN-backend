package com.example.MaiN.dto;

import com.example.MaiN.entity.AiNotice;
import lombok.Getter;

import java.time.LocalDate;


@Getter
public class AiNoticeDto {

    private final Integer id;
    private final String title;
    private final String link;
    private final LocalDate date;

    public AiNoticeDto(int id, String title, String link, LocalDate date) {
        this.id = Math.toIntExact(id);
        this.title = title;
        this.link = link;
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
    public LocalDate getDate() {
        return date;
    }

    // 추가된 from 메서드
    public static AiNoticeDto from(AiNotice aiNotice) {
        return new AiNoticeDto(
                Math.toIntExact(aiNotice.getId()),
                aiNotice.getTitle(),
                aiNotice.getLink(),
                aiNotice.getDate()
        );
    }
}

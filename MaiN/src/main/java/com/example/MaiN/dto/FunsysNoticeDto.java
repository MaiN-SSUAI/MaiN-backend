package com.example.MaiN.dto;

import com.example.MaiN.entity.FunsysNotice;
import lombok.Getter;

import java.time.LocalDate;


@Getter
public class FunsysNoticeDto {

    private final int id;
    private final String title;
    private final String link;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public FunsysNoticeDto(int id, String title, String link, LocalDate startDate, LocalDate endDate) {
        this.id = Math.toIntExact(id);
        this.title = title;
        this.link = link;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public static FunsysNoticeDto from(FunsysNotice funsysNotice) {
        return new FunsysNoticeDto(
                Math.toIntExact(funsysNotice.getId()),
                funsysNotice.getTitle(),
                funsysNotice.getLink(),
                funsysNotice.getStartDate(),
                funsysNotice.getEndDate()
        );
    }
}

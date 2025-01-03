package com.example.MaiN.dto;

import lombok.Getter;

@Getter
public class VersionDto {

    private final String latest;
    private final String present;

    public VersionDto(String latest, String present) {
        this.latest = latest;
        this.present = present;
    }
}

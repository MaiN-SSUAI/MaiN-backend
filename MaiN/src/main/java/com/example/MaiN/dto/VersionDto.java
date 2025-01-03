package com.example.MaiN.dto;

import lombok.Getter;

@Getter
public class VersionDto {

    private final String latest;
    private final String minimum;

    public VersionDto(String latest, String minimum) {
        this.latest = latest;
        this.minimum = minimum;
    }
}

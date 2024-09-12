package com.example.MaiN.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UsaintRequestDto {
    @NotNull
    private String sToken;

    @NotNull
    private String sIdno;

    @NotNull
    private String fcmToken;
}
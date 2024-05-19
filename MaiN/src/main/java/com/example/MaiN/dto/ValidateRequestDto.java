package com.example.MaiN.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ValidateRequestDto {
    private String accessToken;
    private String refreshToken;
}
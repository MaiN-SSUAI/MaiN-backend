package com.example.MaiN.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TokenDto {
    private String accessToken;
    private String refreshToken;
    private String studentId;
}

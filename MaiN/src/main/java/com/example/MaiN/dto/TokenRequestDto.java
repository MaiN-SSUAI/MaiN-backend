package com.example.MaiN.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Data
@Getter
@Setter
public class TokenRequestDto {
    private String accessToken;
    private String refreshToken;
}
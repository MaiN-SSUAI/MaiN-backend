package com.example.MaiN.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginReturnDto {
    private String accessToken;
    private String refreshToken;
    private String studentId;
}

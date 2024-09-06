package com.example.MaiN.dto;

import lombok.Value;

@Value
public class FavoriteRequestDto {
    String studentNo;
    Integer noticeId;
    String noticeType;
}

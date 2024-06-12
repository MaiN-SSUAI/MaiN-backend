package com.example.MaiN.Exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CustomErrorResponse{
    private String message;
    private String code;
    private int status;
    private String detail;

    public CustomErrorResponse(CustomErrorCode code) {
        this.message = code.getMessage();
        this.status = code.getStatus();
        this.code = code.getCode();
        this.detail = code.getDetail();
    }

    public static CustomErrorResponse of(CustomErrorCode code) {
        return new CustomErrorResponse(code);
    }
}
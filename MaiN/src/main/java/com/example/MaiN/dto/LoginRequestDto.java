package com.example.MaiN.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginRequestDto {
    private String studentNo;
    private String studentName;
    private String fcmToken;

    public String getstudentNo() {
        return studentNo;
    }

    public String getstudentName() { return studentName; }

    public void setstudentNo(String studentNo) {
        this.studentNo = studentNo;
    }
}

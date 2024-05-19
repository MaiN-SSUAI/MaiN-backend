package com.example.MaiN.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Setter
public class LoginRequestDto {
    private String studentNo;

    public String getstudentNo() {
        return studentNo;
    }

    public void setstudentNo(String studentNo) {
        this.studentNo = studentNo;
    }


}
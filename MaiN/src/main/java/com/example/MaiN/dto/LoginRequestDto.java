package com.example.MaiN.dto;

import com.example.MaiN.entity.Users;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginRequestDto {
    private String studentId;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }


}

package com.example.MaiN.dto;

import com.example.MaiN.entity.Users;

public class UsersDto {
    private String studentId;

    // Getter 및 Setter
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Users toEntity() {
        return Users.builder()
                .studentId(studentId)
                .build();
    }
}

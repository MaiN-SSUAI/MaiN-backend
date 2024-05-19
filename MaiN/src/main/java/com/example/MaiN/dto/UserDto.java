package com.example.MaiN.dto;

import com.example.MaiN.entity.User;

public class UserDto {
    private String studentId;

    // Getter 및 Setter
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public User toEntity() {
        return User.builder()
                .studentId(studentId)
                .build();
    }
}

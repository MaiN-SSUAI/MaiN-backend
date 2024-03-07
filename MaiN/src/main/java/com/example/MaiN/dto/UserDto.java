package com.example.MaiN.dto;

import com.example.MaiN.model.users;

public class UserDto {
    private String student_id;

    // Getter 및 Setter
    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public users toEntity() {
        return users.builder()
                .student_id(student_id)
                .build();
    }
}
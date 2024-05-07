package com.example.MaiN.dto;

import com.example.MaiN.entity.Users;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UsersDto {
    private String studentId;

    public Users toEntity() {
        return Users.builder()
                .studentId(studentId)
                .build();
    }
}

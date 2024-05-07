package com.example.MaiN.dto;

import com.example.MaiN.entity.Users;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
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

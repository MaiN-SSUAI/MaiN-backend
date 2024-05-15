package com.example.MaiN.dto;

import com.example.MaiN.entity.User;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Data
public class UserDto {
    private String studentNo;

    public User toEntity() {
        return User.builder()
                .studentNo(studentNo)
                .build();
    }
}

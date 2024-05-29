package com.example.MaiN.dto;

import com.example.MaiN.entity.User;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Data
public class UserDto {
    private int id;
    private String studentNo;

    public User toEntity() {
        return User.builder()
                .studentNo(studentNo)
                .build();
    }
}

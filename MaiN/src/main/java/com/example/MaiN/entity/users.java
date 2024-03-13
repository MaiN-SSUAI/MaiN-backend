package com.example.MaiN.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name="users")
public class users {

    @Id
    private String student_id;

    @Builder
    public users(String student_id) {
        this.student_id = student_id;
    }
}
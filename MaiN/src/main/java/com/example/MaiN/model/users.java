package com.example.MaiN.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
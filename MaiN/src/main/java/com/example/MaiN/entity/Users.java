package com.example.MaiN.entity;

import jakarta.persistence.Column;
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
public class Users {

    @Id
    @Column(name = "student_id")
    private String studentId;

    @Builder
    public Users(String studentId) {
        this.studentId = studentId;
    }
}
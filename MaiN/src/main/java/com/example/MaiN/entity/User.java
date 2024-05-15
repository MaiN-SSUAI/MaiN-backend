package com.example.MaiN.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; //user table primary key

    @Column(name = "student_no")
    private String studentNo;

    @Column(name = "name")
    private String studentName;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Builder
    public User(String studentNo) {
        this.studentNo = studentNo;
    }

}
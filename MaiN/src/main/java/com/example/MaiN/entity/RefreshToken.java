package com.example.MaiN.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="refresh_token")
public class RefreshToken {

    @Id
    @Column(name = "student_no")
    private String studentNo;

    @Column(name = "refresh_token")
    private String refreshToken;

}
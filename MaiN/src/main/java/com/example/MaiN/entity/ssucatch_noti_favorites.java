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
@Table(name="ssucatch_noti_favorites")
public class ssucatch_noti_favorites {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // 고유한 기본 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "student_id")
    private users studentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ssucatch_noti_id")
    private ssucatch_noti ssucatchNoti;

    @Builder
    public ssucatch_noti_favorites(users studentId) {
        this.studentId = studentId;
    }

    public void setSsucatchNoti(ssucatch_noti ssucatchNoti) {
        this.ssucatchNoti = ssucatchNoti;
    }
}

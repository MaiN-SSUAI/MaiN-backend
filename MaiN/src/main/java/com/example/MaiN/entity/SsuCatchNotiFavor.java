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
public class SsuCatchNotiFavor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // 고유한 기본 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_no")
    private User studentNo;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ssucatch_noti_id")
    private SsuCatchNoti ssuCatchNoti;

    @Builder
    public SsuCatchNotiFavor(User studentNo) {
        this.studentNo = studentNo;
    }

}

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
@Table(name="funsys_noti_favorites")
public class FunsysNotiFavor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // 고유한 기본 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_no")
    private User studentNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funsys_noti_id")
    private FunsysNoti funsysNoti;

    @Builder
    public FunsysNotiFavor(User studentNo) {
        this.studentNo = studentNo;
    }

    public void setFunsysNoti(FunsysNoti funsysNoti) {
        this.funsysNoti = funsysNoti;
    }

}

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
@Table(name="ai_noti_favorites")
public class AiNotiFavor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // 고유한 기본 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "student_id")
    private Users studentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_noti_id")
    private AiNoti aiNoti;

    @Builder
    public AiNotiFavor(Users studentId) {
        this.studentId = studentId;
    }

    public void setAiNoti(AiNoti aiNoti) {
        this.aiNoti = aiNoti;
    }
}

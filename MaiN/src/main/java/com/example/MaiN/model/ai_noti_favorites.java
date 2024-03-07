package com.example.MaiN.model;

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
public class ai_noti_favorites {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // 고유한 기본 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "student_id")
    private users studentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_noti_id")
    private ai_noti aiNoti;

    @Builder
    public ai_noti_favorites(users studentId) {
        this.studentId = studentId;
    }

    public void setAiNoti(ai_noti aiNoti) {
        this.aiNoti = aiNoti;
    }
}

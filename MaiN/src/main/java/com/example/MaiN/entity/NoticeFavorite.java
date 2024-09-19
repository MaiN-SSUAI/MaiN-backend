package com.example.MaiN.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="notice_favorite")
public class NoticeFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "notice_id")
    private int noticeId;

    @Column(name = "notice_type", nullable = false)
    private String noticeType;

    @Builder
    public NoticeFavorite(User user, Integer noticeId, String noticeType) {
        this.user = user;
        this.noticeId = noticeId;
        this.noticeType = noticeType;
    }

    // 필요한 경우 특정 필드를 변경하는 메서드
    public void updateNoticeFavorite(Integer noticeId, String noticeType) {
        this.noticeId = noticeId;
        this.noticeType = noticeType;
    }
}
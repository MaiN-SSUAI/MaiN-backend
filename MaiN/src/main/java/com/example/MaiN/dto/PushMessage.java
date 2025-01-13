package com.example.MaiN.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PushMessage {
    TEST("푸쉬 알림 개발자 테스트입니다."),

    MIN_LEFT("세미나실 예약 알림\n세미나실 이용 시작 시간이 30분 남았습니다."),

    MIN_LEFT_ENDING("세미나실 예약 알림\n세미나실 이용 종료 시간이 5분 남았습니다. 깨끗한 정리 부탁드립니다.");

    private final String messageTemplate;

    public String formatMessage(Object... args) {
        return String.format(this.messageTemplate, args);
    }
}
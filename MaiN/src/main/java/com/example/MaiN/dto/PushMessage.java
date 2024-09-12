package com.example.MaiN.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PushMessage {
    // 예약 관련 메시지 템플릿
    TEST("개발자 테스트!!!! 수민 하이?"),

    MIN_LEFT("세미나실 예약이 %s에 시작됩니다. 예약 종료 시간은 %s입니다."),

    MIN_LEFT_ENDING("세미나실 사용 종료 5분 전입니다. 마무리와 뒷정리를 부탁드립니다.");

    private final String messageTemplate;

    // 메시지 템플릿을 args로 포맷하는 메서드 추가
    public String formatMessage(Object... args) {
        return String.format(this.messageTemplate, args);
    }
}
package com.example.MaiN.controller;

import com.example.MaiN.dto.FcmRequestDto;
import com.example.MaiN.dto.PushMessage;
import com.example.MaiN.service.FirebaseCloudMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class FcmController {

    private final FirebaseCloudMessageService firebaseCloudMessageService;

    @PostMapping("/fcm")
    public ResponseEntity<?> pushMessage(@RequestBody FcmRequestDto requestDTO) throws IOException {
        try {
            System.out.println(requestDTO.getTargetToken() + " " + requestDTO.getTitle() + " " + requestDTO.getBody());

            // PushMessage 타입으로 변환
            PushMessage pushMessage = PushMessage.valueOf(requestDTO.getTitle());

            // FCM 메시지 전송
            firebaseCloudMessageService.sendMessageTo(
                    requestDTO.getTargetToken(),
                    pushMessage,
                    requestDTO.getBody()
            );
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // 예외 발생 시 로그 출력
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error sending FCM message: " + e.getMessage());
        }
    }
}
package com.example.MaiN.service;

import com.example.MaiN.dto.FcmMessage;
import com.example.MaiN.dto.PushMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FirebaseCloudMessageService {

    private final String API_URL = "https://fcm.googleapis.com/v1/projects/main2-653f5/messages:send";
    private final ObjectMapper objectMapper;

    // 환경 변수에서 firebase_key를 가져오기 위한 설정
    @Value("${FIREBASE_KEY}")
    private String firebaseKeyEncoded;

    public void sendMessageTo(String targetToken, PushMessage pushMessage, Object... args) throws IOException {
        String message = makeMessage(targetToken, pushMessage.formatMessage(args));

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
    }

    private String makeMessage(String targetToken, String body) throws JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(targetToken)
                        .notification(FcmMessage.Notification.builder()
                                .title("알림")
                                .body(body)
                                .image(null)
                                .build()
                        ).build()).validateOnly(false).build();

        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws IOException {
        // 환경 변수에서 Firebase Key를 Base64로 디코딩하여 사용
        byte[] decodedKey = Base64.getDecoder().decode(firebaseKeyEncoded);
        ByteArrayInputStream serviceAccount = new ByteArrayInputStream(decodedKey);

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(serviceAccount)
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        AccessToken accessToken = googleCredentials.refreshAccessToken();
        return accessToken.getTokenValue();
    }
}
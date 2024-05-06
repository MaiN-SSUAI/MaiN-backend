package com.example.MaiN.service;

import com.example.MaiN.dto.*;
import com.example.MaiN.entity.RefreshToken;
import com.example.MaiN.entity.Users;
import com.example.MaiN.repository.RefreshTokenRepository;
import com.example.MaiN.repository.UsersRepository;
import com.example.MaiN.security.JWTProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.security.auth.message.AuthException;
import jakarta.validation.constraints.NotNull;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final OkHttpClient client = new OkHttpClient();
    private final JWTProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    private static final String usaintSSOUrl = "https://saint.ssu.ac.kr/webSSO/sso.jsp";
    private static final String usaintStudentUrl = "https://saint.ssu.ac.kr/webSSUMain/main_student.jsp";

    public UsersService(UsersRepository usersRepository, AuthenticationManagerBuilder authenticationManagerBuilder, JWTProvider jwtProvider, RefreshTokenRepository refreshTokenRepository) {
        this.usersRepository = usersRepository;
        this.jwtProvider = jwtProvider;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public Iterable<Users> findAllUsers() {
        return usersRepository.findAll();
    }

    public String addUser(UsersDto usersDto) {
        Users foundUsers = usersRepository.findByStudentId(usersDto.getStudentId());
        if (foundUsers != null) return "user with this student_id already exists"; //중복 저장 방지
        else {
            Users users = usersDto.toEntity();
            Users saved = usersRepository.save(users);
            return "user info saved";
        }
    }

    //okhttp request 객체 생성
    private Request buildRequest(String url, HashMap<String, String> headers) {
        return new Request.Builder()
                .url(url)
                .headers(Headers.of(headers))
                .get()
                .build();
    }

    public Map<String, Object> usaintAuthService(@NotNull UsaintRequestDto usaintRequestDto) throws Exception {
        String sToken = usaintRequestDto.getSToken();
        String sIdno = usaintRequestDto.getSIdno();


        String cookieRequestUrl = usaintSSOUrl + "?sToken=" + sToken + "&sIdno=" + sIdno;

        //요청 헤더 (쿠키에 sToken, sIdno 추가)
        HashMap<String, String> SSORequestHeaders = new HashMap<>();
        SSORequestHeaders.put("Cookie", "sToken=" + sToken + "; sIdno=" + sIdno);

        String cookieList = fetchCookie(cookieRequestUrl, SSORequestHeaders);

        String stdMajor = fetchMajor(cookieList);

        Map<String, Object> stdInfo = new HashMap<>();
        stdInfo.put("학부", stdMajor);
        stdInfo.put("학번", sIdno);
        return stdInfo;
    }


    //usaint 쿠키 가져오기
    private String fetchCookie(String cookieRequestUrl, HashMap<String, String> SSORequestHeaders) throws Exception {

        Request cookieRequest = buildRequest(cookieRequestUrl, SSORequestHeaders);
        try (Response cookieResponse = client.newCall(cookieRequest).execute()) {

            ResponseBody cookieResponseBody = cookieResponse.body();
            Headers cookieResponseHeaders = cookieResponse.headers();

            if (!cookieResponse.isSuccessful()) throw new Exception("Cookie 요청 실패");

            if (!cookieResponseBody.string().contains("location.href = \"/irj/portal\";")) {
                throw new AuthException("usaint 접근 권한 실패");
            }

            Map<String, List<String>> responseHeaders = cookieResponseHeaders.toMultimap();
            List<String> cookies = responseHeaders.get("set-cookie");
            StringBuilder cookieList = new StringBuilder();

            for (String cookie : cookies) {
                cookie = cookie.split(";")[0];
                cookieList.append(cookie).append(";");
            }

            return cookieList.toString();
        }

    }

    //사용자 소속 확인
    private String fetchMajor(String cookieList) throws Exception {
//        StringBuilder stdInfo = new StringBuilder();
        String stdMajor = "";
        HashMap<String, String> stdInfoRequestHeaders = new HashMap<>();
        stdInfoRequestHeaders.put("Cookie", cookieList);

        Request stdRequest = buildRequest(usaintStudentUrl, stdInfoRequestHeaders);

        try (Response stdInfoResponse = client.newCall(stdRequest).execute()) {
            if (!stdInfoResponse.isSuccessful()) {
                throw new Exception("요청 실패");
            }

            ResponseBody stdInfoResponseBody = stdInfoResponse.body();
            Headers stdInfoResponseHeader = stdInfoResponse.headers();

            if (stdInfoResponseBody == null) {
                throw new Exception("std info response body 가 비었음");
            }

            Document stdInfoDoc = Jsoup.parse(stdInfoResponseBody.string());
            Element stdInfoBox = stdInfoDoc.getElementsByClass("main_box09_con").first();

            if (stdInfoBox == null) {
                throw new Exception("stdInfoBox Empty");
            }

            Elements stdInfoBoxLis = stdInfoBox.getElementsByTag("li");

            for (Element stdInfoBoxLi : stdInfoBoxLis) {
                Element dt = stdInfoBoxLi.getElementsByTag("dt").first();
                if (dt == null) {
                    throw new Exception("dt 태그 내의 데이터 없음");
                }

                Element strong = stdInfoBoxLi.getElementsByTag("strong").first();

                if (dt.text().equals("소속")) {
                    stdMajor = strong.text();
                }

            }

            return stdMajor;
        }
    }

    public String login(@NotNull LoginRequestDto loginRequestDto) {
        String accessToken = jwtProvider.generateAccessToken(loginRequestDto);
        String refreshToken = jwtProvider.generateRefreshToken();

        //refresh 토큰 정보 저장
        RefreshToken refreshTokenDB = RefreshToken.builder()
                .studentId(loginRequestDto.getStudentId())
                .refreshToken(refreshToken)
                .build();

        refreshTokenRepository.save(refreshTokenDB);

        LoginReturnDto loginReturnDto = LoginReturnDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .studentId(loginRequestDto.getStudentId())
            .build();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.writeValueAsString(loginReturnDto);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

//    public ValidateReturnDto validateUser(@NotNull ValidateRequestDto validateRequestDto) throws Exception{
//        boolean isValidToken;
//
//        try{
//            isValidToken = jwtProvider.validateToken(validateRequestDto.getAccessToken());
//        }catch (BadCredentialsException e){
//            throw new Exception("Invalid access token");
//        }
//        catch (ExpiredJwtException e){ //access token 이 만료된 경우
//            Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findById(validateRequestDto.getRefreshToken());
//
//            if (refreshTokenOptional.isEmpty()){
//                throw new Exception("refresh token does not exist");
//            }
//
//            RefreshToken refreshToken = refreshTokenOptional.get();
//            if(!refreshToken.getRefreshToken().equals(validateRequestDto.getAccessToken())){
//                throw new Exception("access token 불일치");
//            }
//
//            Optional<Users> usersOptional = usersRepository.findById(refreshToken.getStudentId());
//            if(usersOptional.isEmpty()){
//                throw new Exception("student doesn't exist");
//            }
//
//            Users users = usersOptional.get();
//
//
//        }
}


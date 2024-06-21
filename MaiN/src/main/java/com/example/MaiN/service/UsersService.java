package com.example.MaiN.service;

import com.example.MaiN.dto.*;
import com.example.MaiN.entity.RefreshToken;
import com.example.MaiN.entity.User;
import com.example.MaiN.repository.RefreshTokenRepository;
import com.example.MaiN.repository.UserRepository;
import com.example.MaiN.security.JWTProvider;
import jakarta.security.auth.message.AuthException;
import jakarta.validation.constraints.NotNull;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.*;

import static java.util.Optional.ofNullable;

@Service
public class UsersService {
    private final UserRepository userRepository;
    private final OkHttpClient client = new OkHttpClient();
    private final JWTProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;


    private static final String usaintSSOUrl = "https://saint.ssu.ac.kr/webSSO/sso.jsp";
    private static final String usaintStudentUrl = "https://saint.ssu.ac.kr/webSSUMain/main_student.jsp";

    public UsersService(UserRepository userRepository, AuthenticationManagerBuilder authenticationManagerBuilder, JWTProvider jwtProvider, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public Iterable<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void addUser(String stdNo,String stdName){
        // 해당 학번이 이미 db에 저장되어 있는지 확인
        User foundUser = userRepository.findByNo(stdNo);
        if(foundUser == null){
            User user = new User();
            user.setStudentNo(stdNo);
            user.setStudentName(stdName);
            userRepository.save(user);
        } else if (foundUser.getStudentName().isEmpty()) {      // db에 저장되어 있을 때 이름이 비어있으면 추가
            foundUser.setStudentName(stdName);
            userRepository.save(foundUser);
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
        String stdMajor = ""; //학부
        String stdNo = ""; //학번
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
            Element stdNameBox = stdInfoDoc.getElementsByClass("main_box09").first();

            if (stdInfoBox == null) {
                throw new Exception("stdInfoBox Empty");
            }

            if(stdNameBox == null){
                throw new Exception("stdNameBox Empty");
            }

            Element nameBoxSpan = stdNameBox.getElementsByTag("span").first();

            String stdName = nameBoxSpan.text();
            stdName = stdName.split("님")[0];

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
                if(dt.text().equals("학번")) {
                    stdNo = strong.text();
                }

                addUser(stdNo,stdName); //파싱한 학번, 이름 저장

            }

            return stdMajor;
        }
    }

    public TokenDto login(@NotNull LoginRequestDto loginRequestDto) {
        String stdNo = loginRequestDto.getstudentNo();
        String accessToken = jwtProvider.generateAccessToken(stdNo);
        String refreshToken = jwtProvider.generateRefreshToken();

        //refresh token을 db 에 저장
        //Optional<User> userOptional = userRepository.findByStudentNo(stdNo);
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByNo((stdNo)));
        if(userOptional.isEmpty()){
            throw new RuntimeException("user not found with studentNo :" + stdNo);
        }
        User user = userOptional.get();

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return TokenDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .studentNo(loginRequestDto.getstudentNo())
            .build();

    }

    public TokenDto reissue(@NotNull TokenRequestDto tokenRequestDto) throws Exception {
        //Refresh Token 검증
        if(!jwtProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token 이 유효하지 않음");
        }

        //access token 에서 인증 정보 가져오기
        Authentication authentication = jwtProvider.getAuthentication(tokenRequestDto.getAccessToken());
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String stdNo = userDetails.getUsername();

        Optional<User> userOptional = userRepository.findByStudentNo(stdNo);
        User user = userOptional.orElseThrow(() -> new NoSuchElementException("User not found for studentId: " + stdNo));

        if(user == null){
            throw new RuntimeException("존재하지 않는 사용자");
        }

        if(user.getRefreshToken() == null){
            throw new RuntimeException("로그아웃된 사용자");
        }

        if(!user.getRefreshToken().equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("refresh token이 일치하지 않음");
        }

        //일치한다면 새로운 access token 생성
        String newAccessToken = jwtProvider.generateAccessToken(stdNo);

        TokenDto tokenDto = TokenDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(String.valueOf(user.getRefreshToken()))
                .studentNo(stdNo)
                .build();

        return tokenDto;
    }

    //로그아웃
    public boolean logout(String stdNo){
        Optional<User> userOptional = userRepository.findByStudentNo(stdNo)
                .filter(user->user.getRefreshToken() != null);

        if(userOptional.isPresent()) {
            User user = userOptional.get();
            user.setRefreshToken(null);
            userRepository.save(user);

            //refresh token 이 정상적으로 삭제되었는지 확인
            Optional<User> updatedUser = userRepository.findByStudentNo(stdNo);
            return updatedUser.map(u->u.getRefreshToken() == null).orElse(false);
        }
        else{
            return false;
        }

    }

}


package com.example.MaiN.security;

import com.example.MaiN.dto.LoginRequestDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;


//토큰 생성 및 검증을 담당하는 클래스
@Component
public class JWTProvider {

    //access token 유효기간
    @Value("${jwt.expiration}")
    private Long JWT_EXPIRATION;

    //refresh token 유효기간
    @Value("${jwt.refresh-expiration}")
    private Long REFRESH_EXPIRATION;

//    @Value("${jwt.secret")
//    private String secret_key;

    //토큰 secret key
    private Key JWT_SECRET;
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";

    private SecretKey getSigningKey(){
        String secret_key = "81be7ddae02bd22cd41d1d7492e1c7e4841445587b65829b787801b49583dd38d26c063a43bca8c568314d0610421d9f0e1fa9b13ac05e5533a7622842710fa1";
        byte[] keyBytes = new byte[secret_key.length()/2];
        this.JWT_SECRET = Keys.hmacShaKeyFor(secret_key.getBytes(StandardCharsets.UTF_8));
//         this.JWT_SECRET = Jwts.SIG.HS256.key().build();
        return (SecretKey) this.JWT_SECRET;
    }

    //access token 생성
    @NotNull
    public String generateAccessToken(@NotNull LoginRequestDto loginRequestDto){

        Date now = new Date();
        Date expiration = new Date(now.getTime() + this.JWT_EXPIRATION);

//        Claims claims = (Claims) Jwts.claims();
//        claims.put("studentId",loginRequestDto.getStudentId());
//        Claims claims = (Claims) Jwts.claims().setSubject(loginRequestDto.getStudentId());


        return Jwts.builder()
                .claim("stduentId",loginRequestDto.getStudentId())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(this.getSigningKey())
                .compact();
    }

    //refresh 토큰 생성
    public String generateRefreshToken(){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + this.REFRESH_EXPIRATION);

        return Jwts.builder()
                .issuedAt(now)
                .expiration(expiration)
                .signWith(this.getSigningKey())
                .compact();
    }

    //토큰 검증
    public boolean validateToken(@NotNull String token) throws Exception {
        try{
            Jwts.parser().verifyWith((SecretKey) JWT_SECRET).build().parseSignedClaims(token);
            return true;
        }
        catch (SignatureException e){
            throw new BadCredentialsException("유효하지 않은 secret key", e);
        }
        catch (ExpiredJwtException e){
            throw e;
        }
        catch (UnsupportedJwtException e){
            throw new UnsupportedJwtException("지원되지 않는 토큰",e);
        }
        catch (Exception e){
            throw new Exception("토큰에 문자 발생",e);
        }

    }

    //토큰 복호화
    public Authentication getAuthentication(String accessToken){
        Claims claims = parseClaims(accessToken);

//        if(claims.get(AUTHORITIES_KEY) == null){
//            throw new RuntimeException("권한 정보가 없는 토큰");
//        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        String studentId = (String) claims.get("studentID");

        UserDetails principal = new User(studentId,"",authorities);
        return new UsernamePasswordAuthenticationToken(principal,"",authorities);
    }

    //access token 의 클레임 정보를 추출하여 파싱
    private Claims parseClaims(String accessToken){
        try{
            return Jwts.parser().verifyWith((SecretKey) JWT_SECRET).build().parseSignedClaims(accessToken).getPayload();
        }
        catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }

}
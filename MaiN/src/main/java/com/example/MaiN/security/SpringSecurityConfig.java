package com.example.MaiN.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Configuration
@EnableWebSecurity // Spring Security를 활성화하는 어노테이션입니다.
@RequiredArgsConstructor
public class SpringSecurityConfig {
    private final JWTFilter jwtFilter;

    //Security filter chain config
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable) //http 기본 인증 비활성화
                .formLogin(AbstractHttpConfigurer::disable) //form 으로 로그인 비활성화
                .csrf(AbstractHttpConfigurer::disable) //csrf 보호 비활성화
                .sessionManagement(AbstractHttpConfigurer::disable) //세션 사용하지 않음
                .cors(Customizer.withDefaults()) //CORS 기본으로 설정
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests.requestMatchers("/users/login").permitAll()//이 API는 요청 허용
                                .anyRequest().authenticated()) //나머지 API 는 인증이 되어야 요청 가능
                .exceptionHandling((exceptionHandling) -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    //권한없이 접근했을 때 접근 거부 상황 처리
    private AccessDeniedHandler accessDeniedHandler = null;

    private AccessDeniedHandler getAccessDeniedHandler() {
        if(accessDeniedHandler == null){
            return accessDeniedHandler = (request, response, accessDeniedException) -> {
                response.sendError(HttpServletResponse.SC_FORBIDDEN); //403에러 발생
            };
        }
        return accessDeniedHandler;
    }

    //사용자 인증이 실패한 경우
    private AuthenticationEntryPoint authenticationEntryPoint = null;
    private AuthenticationEntryPoint getUnathorizedEntryPoint(){
        if(authenticationEntryPoint == null){
            return authenticationEntryPoint = (request, response, authException) -> {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED); //401 에러 발생
            };
        }
        return authenticationEntryPoint;
    }

}

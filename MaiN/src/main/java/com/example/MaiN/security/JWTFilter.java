package com.example.MaiN.security;
//JWT 필터링 커스텀

//import com.example.MaiN.dto.ValidateReturnDto;
import com.example.MaiN.service.UsersService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String PREFIX = "Bearer";

    private final JWTProvider jwtProvider;

    //필터링 로직
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().equals("/users/login")) {
            filterChain.doFilter(request,response);
            return;
        }

        //Access token 가져오기
        try {
            Optional<String> accessToken = getAccessToken(request); //request header 에서 토큰 가져오기

            //access token 이 존재하지 않는 경우 예외 발생
            if(accessToken.isEmpty()) {
                throw new Exception("no token provided");
            }

            //access token 유효성 검증
            if(jwtProvider.validateToken(String.valueOf(accessToken))) {
                //해당 토큰의 Authentication 을 가져와 SecurityContext 에 저장
                Authentication authentication = jwtProvider.getAuthentication(String.valueOf(accessToken));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            SecurityContextHolder.getContext().setAuthentication(null);
            throw new RuntimeException(e);
        }

        filterChain.doFilter(request,response);
    }

    //request header 에서 Access Token 추출하기
    private Optional<String> getAccessToken(@NotNull  HttpServletRequest request){
        Optional<String> token = Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER));

        if(token.isPresent() && token.get().startsWith(PREFIX)) {
            return Optional.of(token.get().substring(PREFIX.length()));
        }

        return Optional.empty();

    }
}

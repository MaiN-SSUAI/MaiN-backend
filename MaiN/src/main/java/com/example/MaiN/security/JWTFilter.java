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

import javax.swing.text.html.Option;
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

        String path = request.getRequestURI();

        if(path.equals("/users/login")|| path.startsWith("/swagger") || path.startsWith("/api-docs") || path.startsWith("/v3")) {
            filterChain.doFilter(request,response);
            return;
        }

        //Access token 가져오기
        try {
            String accessToken = getAccessToken(request);

            if(accessToken == null) {
                throw new Exception("no token provided");
            }

            //access token 유효성 검증
            if(jwtProvider.validateToken(accessToken)) {
                //해당 토큰의 Authentication 을 가져와 SecurityContext 에 저장
                Authentication authentication = jwtProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            SecurityContextHolder.getContext().setAuthentication(null);
            throw new RuntimeException(e);
        }

        filterChain.doFilter(request,response);
    }

    private String getAccessToken(@NotNull HttpServletRequest request){
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(PREFIX)){
            return bearerToken.split(" ")[1].trim();
        }
        return null;
    }
}
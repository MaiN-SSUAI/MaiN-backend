package com.example.MaiN.security;
//JWT 필터링 커스텀

//import com.example.MaiN.dto.ValidateReturnDto;
import com.example.MaiN.service.UsersService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.ErrorResponse;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (path.equals("/users/login") || path.equals("/users/reissue") || path.startsWith("/swagger") || path.startsWith("/api-docs") || path.startsWith("/v3") || path.startsWith("/version")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String accessToken = getAccessToken(request);

            if (accessToken == null) {
                sendErrorResponse(response,HttpServletResponse.SC_FORBIDDEN,"No Token Provided");
                return;
            }

            if (jwtProvider.validateToken(accessToken)) {
                Authentication authentication = jwtProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException e) {
            SecurityContextHolder.clearContext();
            sendErrorResponse(response,HttpServletResponse.SC_UNAUTHORIZED,"Token Expired");
            return;
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            sendErrorResponse(response,HttpServletResponse.SC_FORBIDDEN,"Wrong Token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getAccessToken(@NotNull HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(PREFIX)) {
            return bearerToken.split(" ")[1].trim();
        }
        return null;
    }

    private void sendErrorResponse(HttpServletResponse response,int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(status,message);
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(jsonResponse);

    }

    @Getter
    public static class ErrorResponse{
        private final int status;
        private final String message;


        public ErrorResponse(int status, String message) {
            this.status = status;
            this.message = message;
        }

    }
}

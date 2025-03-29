package com.gotcharoom.gdp.global.security;

import com.gotcharoom.gdp.global.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /*
     * SecurityContextHolder는 기본적으로 ThreadLocal을 사용하여 저장됨.
     * 때문에 Exclude API 설정 시 반드시 로그인 시 사용하는 API / 로그인이 불필요한 API를 분리해야 함
     * ex) 중복체크 API
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        List<String> swaggerPaths = List.of(
                "/swagger",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/api-docs",
                "/api-docs/**",
                "/v3/api-docs/**"
        );

        List<String> excludedPaths = List.of(
                "/api/v1/auth/login",
                "/api/v1/auth/logout",
                "/api/v1/auth/refresh",
                "/api/v1/auth/check",
                "/api/v1/auth/remember-me",
                "/api/v1/mail/find/id",
                "/api/v1/mail/generate/temp-password",
                "/api/v1/user/sign-up",
                "/api/v1/user/check/duplicate/id",
                "/api/v1/user/check/duplicate/nickname",
                "/api/v1/user/check/duplicate/email"
        );

        return swaggerPaths.stream().anyMatch(path::startsWith)  || excludedPaths.stream().anyMatch(path::startsWith);
    }

    // TODO. [TR-YOO] Exception 수정하기
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtUtil.resolveAccessToken(request);
        boolean isBlocked = jwtUtil.isBlacklisted(token);

        try {
            if (token != null && jwtUtil.validateToken(token) && !isBlocked) {
                Authentication auth = jwtUtil.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth); // 정상 토큰이면 SecurityContext에 저장
            }

            filterChain.doFilter(request, response);
        } catch (RedisConnectionFailureException e) {
            SecurityContextHolder.clearContext();
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }
}

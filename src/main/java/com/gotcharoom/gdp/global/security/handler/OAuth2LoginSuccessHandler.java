package com.gotcharoom.gdp.global.security.handler;

import com.gotcharoom.gdp.auth.model.JwtToken;
import com.gotcharoom.gdp.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${gdp.application.front-uri}")
    private String GDP_FRONT_URI;

    private final JwtUtil jwtUtil;

    public OAuth2LoginSuccessHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("OAuth2 로그인 성공 - 사용자: {}", authentication.getName());

        try {
            String accessToken = jwtUtil.createAccessToken(authentication);
            String refreshToken = jwtUtil.createRefreshToken(authentication);

            log.info("AccessToken 및 RefreshToken 생성 완료");

            JwtToken jwtToken = JwtToken.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            boolean isRememberMe = jwtUtil.resolveRememberMe(request, response);
            log.info("RememberMe 상태 확인 - isRememberMe: {}", isRememberMe);

            jwtUtil.setRefreshTokenCookie(jwtToken, response, isRememberMe);
            jwtUtil.setAccessTokenCookie(jwtToken, response, isRememberMe);

            log.info("AccessToken 및 RefreshToken 쿠키 설정 완료");

            String redirectUrl = GDP_FRONT_URI + "/";
            response.sendRedirect(redirectUrl);
            log.info("리다이렉트 완료 - 경로: {}", redirectUrl);

        } catch (Exception e) {
            log.info("OAuth2 로그인 성공 처리 중 예외 발생: {}", e.getMessage());
            throw e;
        }
    }
}

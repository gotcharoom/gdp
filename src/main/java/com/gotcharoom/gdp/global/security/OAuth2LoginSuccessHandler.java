package com.gotcharoom.gdp.global.security;

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
        log.info("OAuth2 Login 성공!");

        try {
            String accessToken = jwtUtil.createAccessToken(authentication);
            String refreshToken = jwtUtil.createRefreshToken(authentication);

            JwtToken jwtToken = JwtToken.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            jwtUtil.setRefreshTokenCookie(jwtToken, response);
            jwtUtil.setAccessTokenCookie(jwtToken, response);

            response.sendRedirect(GDP_FRONT_URI+"/");
        } catch (Exception e) {
            throw e;
        }
    }
}
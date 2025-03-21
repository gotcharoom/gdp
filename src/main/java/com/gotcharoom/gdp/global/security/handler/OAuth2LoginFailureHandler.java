package com.gotcharoom.gdp.global.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    @Value("${gdp.application.front-uri}")
    private String GDP_FRONT_URI;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("소셜 로그인 실패 - URI: {}, Method: {}, 에러 메시지: {}",
                request.getRequestURI(),
                request.getMethod(),
                exception.getMessage());

        String redirectUrl = GDP_FRONT_URI + "/error";
        log.info("소셜 로그인 실패 시 리다이렉트 경로: {}", redirectUrl);

        response.sendRedirect(redirectUrl);
        log.info("리다이렉트 완료");
    }
}
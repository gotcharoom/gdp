package com.gotcharoom.gdp.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotcharoom.gdp.global.api.ApiResponse;
import com.gotcharoom.gdp.global.api.ErrorResponse;
import com.gotcharoom.gdp.global.util.ObjectUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(ObjectUtil objectUtil) {
        this.objectMapper = objectUtil.getObjectMapper();
    }

    // 유저 정보 없이 접근한 경우 : SC_UNAUTHORIZED (401) 응답
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.info("인증되지 않은 사용자 접근 차단 - URI: {}, Method: {}, Message: {}",
                request.getRequestURI(), request.getMethod(), authException.getMessage());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiResponse<Object> errorResponse = ApiResponse.error(ErrorResponse.LOGIN_UNAUTHORIZED);
        String responseBody = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(responseBody);
        log.info("401 Unauthorized 응답 반환 완료");
    }
}

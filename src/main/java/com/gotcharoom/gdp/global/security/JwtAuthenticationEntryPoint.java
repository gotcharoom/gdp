package com.gotcharoom.gdp.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotcharoom.gdp.global.api.ApiResponse;
import com.gotcharoom.gdp.global.api.ErrorResponse;
import com.gotcharoom.gdp.global.util.ObjectUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    public JwtAuthenticationEntryPoint(ObjectUtil objectUtil) {
        this.objectMapper = objectUtil.getObjectMapper();
    }

    // 유저 정보 없이 접근한 경우 : SC_UNAUTHORIZED (401) 응답
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ApiResponse<Object> errorResponse = ApiResponse.error(ErrorResponse.LOGIN_UNAUTHORIZED);

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
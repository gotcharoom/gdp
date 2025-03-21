package com.gotcharoom.gdp.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotcharoom.gdp.global.api.ApiResponse;
import com.gotcharoom.gdp.global.api.ErrorResponse;
import com.gotcharoom.gdp.global.util.ObjectUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public JwtAccessDeniedHandler(ObjectUtil objectUtil) {
        this.objectMapper = objectUtil.getObjectMapper();
    }

    // 필요한 권한 없이 접근한 경우 : SC_FORBIDDEN(403) 응답
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        log.info("권한 없는 사용자 접근 차단 - URI: {}, Method: {}", request.getRequestURI(), request.getMethod());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        ApiResponse<Object> errorResponse = ApiResponse.error(ErrorResponse.LOGIN_FORBIDDEN);
        String responseBody = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(responseBody);
        log.info("403 Forbidden 응답 반환 완료");
    }
}

package com.gotcharoom.gdp.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotcharoom.gdp.global.api.ApiResponse;
import com.gotcharoom.gdp.global.api.ErrorResponse;
import com.gotcharoom.gdp.global.util.ObjectUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    public JwtAccessDeniedHandler(ObjectUtil objectUtil) {
        this.objectMapper = objectUtil.getObjectMapper();
    }

    // 필요한 권한 없이 접근한 경우 : SC_FORBIDDEN(403) 응답
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        ApiResponse<Object> errorResponse = ApiResponse.error(ErrorResponse.LOGIN_FORBIDDEN);

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
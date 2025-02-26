package com.gotcharoom.gdp.login.controller;

import com.gotcharoom.gdp.global.api.ApiResponse;
import com.gotcharoom.gdp.login.model.GdpLoginRequest;
import com.gotcharoom.gdp.login.model.JwtToken;
import com.gotcharoom.gdp.login.service.GdpLoginService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/gdp")
@Tag(name = "GDP 로그인", description = "GDP 회원 로그인 API")
public class GdpLoginController {

    private final GdpLoginService gdpLoginService;

    public GdpLoginController(GdpLoginService gdpLoginService) {

        this.gdpLoginService = gdpLoginService;
    }

    @PostMapping("/login")
    public ApiResponse<JwtToken> login(@RequestBody GdpLoginRequest gdpLoginRequest) {
        log.info("JWT 생성 시작: {}", gdpLoginRequest.getId());
        try {
            JwtToken jwtToken = gdpLoginService.generateJwtToken(gdpLoginRequest);

            return ApiResponse.success(jwtToken);
        } catch (Exception e) {

            throw new RuntimeException("로그인 실패");
        }
    }
}

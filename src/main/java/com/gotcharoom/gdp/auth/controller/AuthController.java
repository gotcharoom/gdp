package com.gotcharoom.gdp.auth.controller;

import com.gotcharoom.gdp.global.api.ApiResponse;
import com.gotcharoom.gdp.auth.model.GdpLoginRequest;
import com.gotcharoom.gdp.auth.model.JwtToken;
import com.gotcharoom.gdp.auth.model.LoginUserInfoResponse;
import com.gotcharoom.gdp.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "로그인 기능", description = "GDP 회원 로그인 API")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {

        this.authService = authService;
    }

    @Operation(
            summary = "로그인 - JWT 토큰 발급",
            description = "JWT 토큰 통한 자체 로그인 API"
    )
    @PostMapping("/login")
    public ApiResponse<JwtToken> login(@RequestBody GdpLoginRequest gdpLoginRequest) {

        try {
            JwtToken jwtToken = authService.generateJwtToken(gdpLoginRequest);

            return ApiResponse.success(jwtToken);
        } catch (Exception e) {

            throw new RuntimeException("로그인 실패");
        }
    }

    @Operation(
            summary = "로그인 - 현재 로그인한 유저 정보",
            description = "현재 로그인한 유저의 상세 정보 API"
    )
    @GetMapping("/info")
    public ApiResponse<LoginUserInfoResponse> getLoginUserInfo() {

        try {
            LoginUserInfoResponse loginUserInfoResponse = authService.getLoginUserDetail();

            return ApiResponse.success(loginUserInfoResponse);
        } catch (Exception e) {

            throw new RuntimeException("계정 정보를 가져오는데 실패");
        }
    }

    // TODO. [TR-YOO] 로그아웃 기능 구현하기


    @Operation(
            summary = "JWT Access Token 갱신",
            description = "JWT Access Token 갱신 API"
    )
    @PostMapping("/refresh")
    public ApiResponse<JwtToken> refreshToken(HttpServletRequest request) {

        try {

            JwtToken jwtToken = authService.replaceJwtToken(request);

            return ApiResponse.success(jwtToken);
        } catch (Exception e) {

            throw new RuntimeException("로그인 실패");
        }
    }
}

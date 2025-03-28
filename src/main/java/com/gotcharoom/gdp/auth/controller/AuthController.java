package com.gotcharoom.gdp.auth.controller;

import com.gotcharoom.gdp.auth.model.*;
import com.gotcharoom.gdp.global.api.ApiResponse;
import com.gotcharoom.gdp.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "로그인 기능", description = "GDP 회원 로그인 API")
public class AuthController {

    @Value("${auth.token.location:COOKIE}")
    private TokenLocationEnum tokenLocation;

    private final AuthService authService;

    public AuthController(AuthService authService) {

        this.authService = authService;
    }

    @Operation(
            summary = "로그인 - JWT 토큰 발급",
            description = "JWT 토큰 통한 자체 로그인 API"
    )
    @PostMapping("/login")
    public ApiResponse<JwtToken> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {

        try {
            JwtToken jwtToken = authService.generateJwtToken(loginRequest);

            if(tokenLocation == TokenLocationEnum.COOKIE) {
                authService.setAllToken(jwtToken, request, response);

                jwtToken = null;
            }

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
            summary = "로그아웃",
            description = "로그아웃 API API"
    )
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {

        try {
            authService.executeLogout(request);
            authService.removeAllToken(response);

            return ApiResponse.success();
        } catch (Exception e) {

            throw new RuntimeException("로그아웃 실패");
        }
    }

    @Operation(
            summary = "JWT Access Token 갱신",
            description = "JWT Access Token 갱신 API"
    )
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JwtToken>> refreshToken(HttpServletRequest request, HttpServletResponse response) {

        try {

            JwtToken jwtToken = authService.replaceJwtToken(request);

            if(tokenLocation == TokenLocationEnum.COOKIE) {
                authService.setAccessToken(jwtToken, request, response);
                jwtToken = null;
            }

            return ResponseEntity.ok(ApiResponse.success(jwtToken));
        } catch (Exception e) {

           return ResponseEntity.status(499)
                   .body(ApiResponse.error());
        }
    }

    @Operation(
            summary = "로그인 시 Access Token이 유효한지 확인",
            description = "로그인 시 JWT의 Access Token이 유효한지 확인하는 API"
    )
    @PostMapping("/check")
    public ApiResponse<Boolean> checkToken(HttpServletRequest request) {

        try {
            boolean isChecked = authService.checkAccessToken(request);

            return ApiResponse.success(isChecked);
        } catch (Exception e) {

            return ApiResponse.error();
        }
    }

    @Operation(
            summary = "Remember Me Cookie 설정",
            description = "Token 기간 조절하는 Remember Me Cookie 설정용 API"
    )
    @PostMapping("/remember-me")
    public ApiResponse<Void> rememberMe(@RequestBody RememberMeRequest request, HttpServletResponse response) {

        try {
            authService.setRememberMeCookie(request, response);

            return ApiResponse.success();
        } catch (Exception e) {

            return ApiResponse.error();
        }
    }
}

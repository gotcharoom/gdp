package com.gotcharoom.gdp.platform.controller;

import com.gotcharoom.gdp.global.api.ApiResponse;
import com.gotcharoom.gdp.platform.model.PlatformCallbackRequest;
import com.gotcharoom.gdp.platform.model.PlatformType;
import com.gotcharoom.gdp.platform.model.SteamRequest;
import com.gotcharoom.gdp.platform.service.PlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/connect")
@Tag(name = "플랫폼 연동", description = "플랫폼 연동 API 컨트롤러")
public class ConnectController {

    @Value("${gdp.application.front-uri}")
    private String FRONT_URI;

    private final String USER_INFO_PAGE = "/user/info";

    private final PlatformService platformService;

    public ConnectController(PlatformService platformService) {
        this.platformService = platformService;
    }

    @Operation(
            summary = "연동 확인용 쿠키 제거",
            description = "연동 확인용 쿠키 제거 API"
    )
    @PutMapping("/connection-cookie/clear")
    public ApiResponse<Void> clearConnectionCookie(HttpServletResponse response) {

        platformService.removeConnectionCookie(response);

        return ApiResponse.success();
    }

    /* STEAM */

    @Operation(
            summary = "스팀 연결 콜백",
            description = "스팀 연결 콜백 API"
    )
    @GetMapping("/steam/callback")
    public Void connectSteam(@RequestParam Map<String, String> params, HttpServletResponse response) {

        SteamRequest request = SteamRequest.fromParams(params);

        platformService.connectPlatform(PlatformType.STEAM, request, response);

        try {

            response.sendRedirect(FRONT_URI + USER_INFO_PAGE);

        } catch (IOException e) {
            throw new RuntimeException("리디렉션 실패", e);
        }

        return null;
    }

    @Operation(
            summary = "개발용 - 스팀 (Dev 환경) 연결 콜백",
            description = "스팀 (Dev 환경) 콜백 API"
    )
    @GetMapping("/steam-dev/callback")
    public Void connectSteamDev(@RequestParam Map<String, String> params, HttpServletResponse response) {

        SteamRequest request = SteamRequest.fromParams(params);

        platformService.connectPlatform(PlatformType.STEAM_DEV, request, response);

        try {

            response.sendRedirect(FRONT_URI + USER_INFO_PAGE);

        } catch (IOException e) {
            throw new RuntimeException("리디렉션 실패", e);
        }

        return null;
    }

    @Operation(
            summary = "개발용 - 스팀 (Dev 환경) 연결 콜백",
            description = "스팀 (Dev 환경) 콜백 API"
    )
    @GetMapping("/steam-local/callback")
    public Void connectSteamLocal(@RequestParam Map<String, String> params, HttpServletResponse response) {

        SteamRequest request = SteamRequest.fromParams(params);

        platformService.connectPlatform(PlatformType.STEAM_LOCAL, request, response);

        try {

            response.sendRedirect(FRONT_URI + USER_INFO_PAGE);

        } catch (IOException e) {
            throw new RuntimeException("리디렉션 실패", e);
        }

        return null;
    }
}

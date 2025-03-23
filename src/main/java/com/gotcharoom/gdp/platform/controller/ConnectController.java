package com.gotcharoom.gdp.platform.controller;

import com.gotcharoom.gdp.global.api.ApiResponse;
import com.gotcharoom.gdp.platform.model.PlatformCallbackRequest;
import com.gotcharoom.gdp.platform.model.PlatformType;
import com.gotcharoom.gdp.platform.model.SteamRequest;
import com.gotcharoom.gdp.platform.service.PlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/connect")
@Tag(name = "플랫폼 연동", description = "플랫폼 연동 API 컨트롤러")
public class ConnectController {

    private final PlatformService platformService;

    public ConnectController(PlatformService platformService) {
        this.platformService = platformService;
    }

    @Operation(
            summary = "플랫폼 연결 콜백",
            description = "플랫폼 연결 콜백 API"
    )
    @GetMapping("/steam/callback")
    public ApiResponse<Void> connectSteam(@ModelAttribute SteamRequest request, HttpServletResponse response) {

        platformService.connectPlatform(PlatformType.STEAM, request, response);

        return ApiResponse.success();
    }
}

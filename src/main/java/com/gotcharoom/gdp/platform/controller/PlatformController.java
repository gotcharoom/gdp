package com.gotcharoom.gdp.platform.controller;

import com.gotcharoom.gdp.global.api.ApiResponse;
import com.gotcharoom.gdp.platform.model.*;
import com.gotcharoom.gdp.platform.service.PlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/platform")
@Tag(name = "플랫폼 연동 정보", description = "플랫폼 연동 정보 API 컨트롤러")
public class PlatformController {

    private final PlatformService platformService;

    public PlatformController(PlatformService platformService) {
        this.platformService = platformService;
    }

    @Operation(
            summary = "플랫폼 등록",
            description = "플랫폼 등록 API"
    )
    @PostMapping("/platform")
    public ApiResponse<Void> postPlatform(@RequestBody PlatformCreateRequest request) {

        platformService.createPlatform(request);

        return ApiResponse.success();
    }

    @Operation(
            summary = "플랫폼 목록 조회",
            description = "플랫폼 목록 조회 API"
    )
    @GetMapping("/platform")
    public ApiResponse<List<PlatformResponse>> getPlatforms() {

        List<PlatformResponse> response = platformService.getPlatforms();

        return ApiResponse.success(response);
    }

    @Operation(
            summary = "플랫폼 삭제",
            description = "플랫폼 삭제 API"
    )
    @DeleteMapping("/platform/{id}")
    public ApiResponse<Void> deletePlatform(@PathVariable Long id) {

        platformService.deletePlatform(id);

        return ApiResponse.success();
    }

    @Operation(
            summary = "플랫폼 수정",
            description = "플랫폼 수정 API"
    )
    @PutMapping("/platform")
    public ApiResponse<Void> putPlatform(@RequestBody PlatformModifyRequest request) {

        platformService.modifyPlatform(request);

        return ApiResponse.success();
    }
}

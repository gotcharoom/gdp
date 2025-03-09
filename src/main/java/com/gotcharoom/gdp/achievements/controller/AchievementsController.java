package com.gotcharoom.gdp.achievements.controller;

import com.gotcharoom.gdp.achievements.model.SteamPlayerStat;
import com.gotcharoom.gdp.achievements.service.AchievementService;
import com.gotcharoom.gdp.global.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/v1/achievements")
@AllArgsConstructor
@Tag(name = "achievements", description = "도전과제 API 입니다")
@RestController
public class AchievementsController {

    private final AchievementService achievementService;

    // 스팀과 연동된 내 계정의 도전과제 목록 가져오기 (모든 게임)
    @Operation(
            summary = "도전과제 목록",
            description = "도전과제 목록 불러오기"
    )
    @GetMapping("/r1")
    public ApiResponse<List<SteamPlayerStat>> requestMyAchievement(@AuthenticationPrincipal UserDetails userDetails) {

        // todo. 나중에 유저 이름으로 steamID 불러오는 코드 추가
        // @AuthenticationPrincipal UserDetails userDetails
        // userDetails.getUsername();

        List<SteamPlayerStat> result = achievementService.getSteamPlayerAchievement("ss");
        return ApiResponse.success(result);
    }

    @Operation(
            summary = "테스트",
            description = "테스트 중"
    )
    @GetMapping("/test")
    public ApiResponse<String> test() {
        return ApiResponse.success("string");
    }

}

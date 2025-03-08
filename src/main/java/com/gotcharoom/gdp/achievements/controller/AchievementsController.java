package com.gotcharoom.gdp.achievements.controller;

import com.gotcharoom.gdp.achievements.model.response.SteamAchievementResponse;
import com.gotcharoom.gdp.achievements.model.response.SteamOwnGames;
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
    public ApiResponse<SteamAchievementResponse> requestMyAchievement(@AuthenticationPrincipal UserDetails userDetails) {

        // 로그인 여부 체크
        System.out.println(userDetails.getUsername());
//        if(userDetails == null) throw new Exception("메롱 시티");

        SteamAchievementResponse result = achievementService.GetSteamPlayerAchievementsOne(userDetails.getUsername(), "429660");
        return ApiResponse.success(result);
    }

    @Operation(
            summary = "테스트",
            description = "appid 확인 테스트"
    )
    @GetMapping("/test")
    public ApiResponse<SteamOwnGames> test() {
        // todo. @AuthenticationPrincipal UserDetails userDetails 파라미터를 사용한 유저 데이터 추가 처리 필요

        return ApiResponse.success(achievementService.GetSteamOwnedGames());
    }
}

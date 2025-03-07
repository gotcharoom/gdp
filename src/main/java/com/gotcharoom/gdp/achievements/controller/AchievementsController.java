package com.gotcharoom.gdp.achievements.controller;

import com.gotcharoom.gdp.achievements.model.response.SteamAchievementResponse;
import com.gotcharoom.gdp.achievements.service.AchievementService;
import com.gotcharoom.gdp.global.api.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/achievements")
@AllArgsConstructor
@Tag(name = "achievements", description = "도전과제 API 입니다")
@RestController
public class AchievementsController {

    private final AchievementService achievementService;

    @GetMapping("/r1")
    public ApiResponse<SteamAchievementResponse> requestMyAchievement() {

        SteamAchievementResponse result = achievementService.getSteamAchievement();
        return ApiResponse.success(result);
    }

}

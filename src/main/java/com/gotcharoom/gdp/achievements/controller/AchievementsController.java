package com.gotcharoom.gdp.achievements.controller;

import com.gotcharoom.gdp.achievements.model.SteamPlayerStat;
import com.gotcharoom.gdp.achievements.model.request.AlbumSaveRequest;
import com.gotcharoom.gdp.achievements.service.AchievementService;
import com.gotcharoom.gdp.achievements.service.AlbumService;
import com.gotcharoom.gdp.global.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/achievements")
@AllArgsConstructor
@Tag(name = "achievements", description = "도전과제 API 입니다")
@RestController
public class AchievementsController {

    private final AchievementService achievementService;
    private final AlbumService albumService;

    // 스팀과 연동된 내 계정의 도전과제 목록 가져오기 (모든 게임)
    @Operation(
            summary = "도전과제 목록",
            description = "도전과제 목록 불러오기 / return 타입 : 도전과제 목록(List<SteamPlayerStat>)"
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
            summary = "연동한 도전과제 저장하기",
            description = "연동에 성공한 도전과제를 저장 / return 타입 : 저장한 수(count)"
    )
    @PostMapping("/r2")
    public ApiResponse<Object> saveAchievements() {
        // todo. 리스트 불러오기 -> 프론트 개발 후 프론트측에서 파라미터로 받아오는걸로 추후 변경
        List<SteamPlayerStat> achievementList = achievementService.getSteamPlayerAchievement("ss");

        return ApiResponse.success(achievementService.saveSteamAchievements(achievementList));
    }

    @Operation(
            summary = "앨범에 도전과제 저장하기",
            description = "내 앨범에 선택한 도전과제를 저장 / return 타입 : 저장한 수(count)"
    )
    @PostMapping("/r3")
    public ApiResponse<Integer> saveAlbum(@RequestBody AlbumSaveRequest requestData) {

        AlbumSaveRequest mockData = albumService.albumRequestDataTest();
        return ApiResponse.success(albumService.saveAlbum(mockData));
    }




    // --------------------------------------- TEST API ---------------------------------------

    @Operation(
            summary = "외부 api 테스트 (GetSchemaForGame)",
            description = "테스트 중"
    )
    @GetMapping("/test")
    public ApiResponse<Object> test() {
        return ApiResponse.success(achievementService.test());
    }

    @Operation(
            summary = "외부 api 테스트 (GetOwnedGames)",
            description = "테스트 중"
    )
    @GetMapping("/test2")
    public ApiResponse<Object> test2() {
        return ApiResponse.success(achievementService.test2());
    }

    @Operation(
            summary = "외부 api 테스트 (GetPlayerAchievements)",
            description = "테스트 중"
    )
    @GetMapping("/test3")
    public ApiResponse<Object> test3() {
        return ApiResponse.success(achievementService.getSteamPlayerAchievementsOne(1623730, "76561198230645968"));
    }


}

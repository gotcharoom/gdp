package com.gotcharoom.gdp.achievements.controller;

import com.gotcharoom.gdp.achievements.model.request.DisplayStandSaveRequest;
import com.gotcharoom.gdp.achievements.model.response.DisplayStandGetListResponse;
import com.gotcharoom.gdp.achievements.model.response.DisplayStandGetResponse;
import com.gotcharoom.gdp.achievements.service.DisplayStandService;
import com.gotcharoom.gdp.global.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/displayStand")
@AllArgsConstructor
@Tag(name = "전시대 기능", description = "전시대 관련 API")
@RestController
public class DisplayStandController {
    private final DisplayStandService displayStandService;

    @Operation(
            summary = "전시대 저장하기",
            description = "작성한 전시대 내용과 전시대에 등록한 도전과제를 저장"
    )
    @PostMapping("/r1")
    public ApiResponse<String> saveDisplayStand(@RequestBody DisplayStandSaveRequest requestData) {
        displayStandService.saveUserDisplayStand(requestData);
        return ApiResponse.success("ok");
    }

    @Operation(
            summary = "전시대 수정하기",
            description = "수정한 변경 사항을 저장"
    )
    @PostMapping("/r2/{id}")
    public ApiResponse<String> editDisplayStand(@AuthenticationPrincipal UserDetails userDetails,
                                                @PathVariable Long id,
                                                @RequestBody DisplayStandSaveRequest requestData) {
        displayStandService.editUserDisplayStand(userDetails.getUsername(), id, requestData);
        return ApiResponse.success("ok");
    }

    @Operation(
            summary = "전시대 삭제",
            description = "선택한 전시대 지우기"
    )
    @DeleteMapping("/r3/{id}")
    public ApiResponse<String> deleteDisplayStand(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        displayStandService.deleteUserDisplayStand(userDetails.getUsername(), id);
        return ApiResponse.success("ok");
    }

    @Operation(
            summary = "전시대 가져오기(1건 상세보기)",
            description = "선택한 전시대 정보 가져오기"
    )
    @GetMapping("/r4")
    public ApiResponse<DisplayStandGetResponse> getDisplayStandDetail(@RequestParam("id") Long id) {
        return ApiResponse.success(displayStandService.getUserDisplayStandOne(id));
    }

    @Operation(
            summary = "전시대 목록 가져오기",
            description = "전시대 정보 가져오기"
    )
    @GetMapping("/r5")
    public ApiResponse<PagedModel<DisplayStandGetListResponse>> getDisplayStandList(@RequestParam int pageNo) {
        return ApiResponse.success(new PagedModel<>(displayStandService.getUserDisplayStands(pageNo, 5)));
    }
}

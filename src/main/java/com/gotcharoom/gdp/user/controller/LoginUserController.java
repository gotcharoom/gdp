package com.gotcharoom.gdp.user.controller;

import com.gotcharoom.gdp.auth.model.JwtToken;
import com.gotcharoom.gdp.global.api.ApiResponse;
import com.gotcharoom.gdp.user.model.UserDetailsResponse;
import com.gotcharoom.gdp.user.model.UserDetailsUpdateRequest;
import com.gotcharoom.gdp.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/login-user")
@Tag(name = "GDP 로그인한 유저", description = "GDP 로그인한 회원 API")
public class LoginUserController {

    private final UserService userService;

    public LoginUserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "회원 탈퇴 API"
    )
    @DeleteMapping("/cancel-user")
    public ApiResponse<JwtToken> cancelUser() {

        try {
            userService.deleteUser();

            return ApiResponse.success();
        } catch (Exception e) {

            throw new RuntimeException("유저 생성 실패");
        }
    }

    @Operation(
            summary = "중복 체크 - ID (중복 시 true / 사용 가능시 false)",
            description = "ID 중복체크 API (중복 시 true / 사용 가능시 false)"
    )
    @GetMapping("/check/duplicate/id")
    public ApiResponse<Boolean> checkUserDuplicateId(@RequestParam String id) {

        boolean isDuplicated = userService.checkDuplicateId(id);

        return ApiResponse.success(isDuplicated);
    }

    @Operation(
            summary = "중복 체크 - Nickname (중복 시 true / 사용 가능시 false)",
            description = "닉네임 중복체크 API (중복 시 true / 사용 가능시 false)"
    )
    @GetMapping("/check/duplicate/nickname")
    public ApiResponse<Boolean> checkUserDuplicateNickname(@RequestParam String nickname) {

        boolean isDuplicated = userService.checkDuplicateNickname(nickname);

        return ApiResponse.success(isDuplicated);
    }

    @Operation(
            summary = "중복 체크 - Email (중복 시 true / 사용 가능시 false)",
            description = "Email 중복체크 API (중복 시 true / 사용 가능시 false)"
    )
    @GetMapping("/check/duplicate/email")
    public ApiResponse<Boolean> checkUserDuplicateEmail(@RequestParam String email) {

        boolean isDuplicated = userService.checkDuplicateEmail(email);

        return ApiResponse.success(isDuplicated);
    }

    /* 유저 정보 */

    @Operation(
            summary = "내 정보",
            description = "유저 - 내 정보 API"
    )
    @GetMapping("/details")
    public ApiResponse<UserDetailsResponse> getUserDetails() {

        UserDetailsResponse userDetails = userService.getUserDetails();

        return ApiResponse.success(userDetails);
    }

    /*
    * RequestPart로 File과 Json 객체 분리하는 방법도 좋지만
    * ModelAttribute로 구성 가능한지 확인하고 싶어서 구성한 메소드
    */
    @Operation(
            summary = "내 정보 수정",
            description = "유저 - 내 정보 수정 API"
    )
    @PutMapping("/details")
    public ApiResponse<Void> putUserDetails(@ModelAttribute UserDetailsUpdateRequest request) throws IOException {

        userService.putUserDetails(request);

        return ApiResponse.success();
    }
}

package com.gotcharoom.gdp.user.UserController;

import com.gotcharoom.gdp.global.api.ApiResponse;
import com.gotcharoom.gdp.auth.model.JwtToken;
import com.gotcharoom.gdp.user.model.UserDetailsResponse;
import com.gotcharoom.gdp.user.model.UserDetailsUpdateRequest;
import com.gotcharoom.gdp.user.model.UserSignUpRequest;
import com.gotcharoom.gdp.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "GDP 유저", description = "GDP 회원 API")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "회원 가입",
            description = "회원 가입 API"
    )
    @PostMapping("/sign-up")
    public ApiResponse<Boolean> signUpUser(@RequestBody UserSignUpRequest userSignUpRequest) {

        try {
            boolean isRegistered = userService.registerUser(userSignUpRequest);

            if(!isRegistered) {
                throw new RuntimeException();
            }

            return ApiResponse.success(true);
        } catch (Exception e) {

            throw new RuntimeException("유저 생성 실패");
        }
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
    public ApiResponse<Boolean> checkDuplicateId(@RequestParam String id) {

        boolean isDuplicated = userService.checkDuplicateId(id);

        return ApiResponse.success(isDuplicated);
    }

    @Operation(
            summary = "중복 체크 - Nickname (중복 시 true / 사용 가능시 false)",
            description = "닉네임 중복체크 API (중복 시 true / 사용 가능시 false)"
    )
    @GetMapping("/check/duplicate/nickname")
    public ApiResponse<Boolean> checkDuplicateNickname(@RequestParam String nickname) {

        boolean isDuplicated = userService.checkDuplicateNickname(nickname);

        return ApiResponse.success(isDuplicated);
    }

    @Operation(
            summary = "중복 체크 - Email (중복 시 true / 사용 가능시 false)",
            description = "Email 중복체크 API (중복 시 true / 사용 가능시 false)"
    )
    @GetMapping("/check/duplicate/email")
    public ApiResponse<Boolean> checkDuplicateEmail(@RequestParam String email) {

        boolean isDuplicated = userService.checkDuplicateEmail(email);

        return ApiResponse.success(isDuplicated);
    }

    @Operation(
            summary = "내 정보",
            description = "유저 - 내 정보 API"
    )
    @GetMapping("/details")
    public ApiResponse<UserDetailsResponse> getUserDetails() {

        UserDetailsResponse userDetails = userService.getUserDetails();

        return ApiResponse.success(userDetails);
    }

    @Operation(
            summary = "내 정보 수정",
            description = "유저 - 내 정보 수정 API"
    )
    @PutMapping("/details")
    public ApiResponse<Void> putUserDetails(UserDetailsUpdateRequest request) {


        userService.putUserDetails(request);

        return ApiResponse.success();
    }
}

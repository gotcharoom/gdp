package com.gotcharoom.gdp.user.UserController;

import com.gotcharoom.gdp.global.api.ApiResponse;
import com.gotcharoom.gdp.auth.model.JwtToken;
import com.gotcharoom.gdp.user.model.UserSignUpRequest;
import com.gotcharoom.gdp.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

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
    public ApiResponse<JwtToken> signUpUser(@RequestBody UserSignUpRequest userSignUpRequest) {

        try {
            userService.registerUser(userSignUpRequest);

            return ApiResponse.success();
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
}

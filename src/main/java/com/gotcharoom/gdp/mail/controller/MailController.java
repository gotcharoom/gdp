package com.gotcharoom.gdp.mail.controller;

import com.gotcharoom.gdp.auth.model.JwtToken;
import com.gotcharoom.gdp.auth.model.LoginRequest;
import com.gotcharoom.gdp.auth.model.TokenLocationEnum;
import com.gotcharoom.gdp.global.api.ApiResponse;
import com.gotcharoom.gdp.mail.model.FindIdRequest;
import com.gotcharoom.gdp.mail.model.FindResponse;
import com.gotcharoom.gdp.mail.service.MailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/mail")
@Tag(name = "메일 보내기 기능", description = "GDP 메일 보내기")
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @Operation(
            summary = "로그인 - JWT 토큰 발급",
            description = "JWT 토큰 통한 자체 로그인 API"
    )
    @PostMapping("/find/id")
    public ApiResponse<FindResponse> login(@RequestBody FindIdRequest findIdRequest) throws MessagingException {

        FindResponse response = mailService.sendFindIdEmail(findIdRequest);
        return ApiResponse.success(response);
    }
}

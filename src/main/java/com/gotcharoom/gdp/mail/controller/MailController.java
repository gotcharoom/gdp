package com.gotcharoom.gdp.mail.controller;

import com.gotcharoom.gdp.global.api.ApiResponse;
import com.gotcharoom.gdp.mail.model.FindIdRequest;
import com.gotcharoom.gdp.mail.model.FindPasswordRequest;
import com.gotcharoom.gdp.mail.model.FindResponse;
import com.gotcharoom.gdp.mail.service.MailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
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
            summary = "ID 찾기",
            description = "Email 통한 GDP ID 찾기 API"
    )
    @PostMapping("/find/id")
    public ApiResponse<FindResponse> findId(@RequestBody FindIdRequest request) throws MessagingException {

        FindResponse response = mailService.sendFindIdEmail(request);
        return ApiResponse.success(response);
    }

    @Operation(
            summary = "임시 비밀번호 발급",
            description = "GDP ID / Email 통한 임시 비밀번호 발급 API"
    )
    @PostMapping("/generate/temp-password")
    public ApiResponse<FindResponse> generateTemporaryPassword(@RequestBody FindPasswordRequest request) throws MessagingException {

        FindResponse response = mailService.sendGenerateTempPasswordEmail(request);
        return ApiResponse.success(response);
    }
}

package com.gotcharoom.gdp.global.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorResponse {
    INTERNAL_SERVER_ERROR(9999, "API 호출 중 오류가 발생했습니다"),
    NOT_FOUND_END_POINT(9991, "요청한 API를 찾을 수 없습니다"),
    TEST_FAIL(9998, "Test 실패"),
    SAMPLE_ERROR(9997, "SAMPLEE RROR"),
    LOGIN_UNAUTHORIZED(1401, "인증되지 않은 로그인"),
    LOGIN_FORBIDDEN(1403, "권한 없음"),
    PASSWORD_CHANGE_GDP_USER_ONLY(2001, "GDP 유저만 비밀번호를 변경할 수 있습니다"),
    PASSWORD_CHANGE_NOT_CORRESPOND_CURRENT_PASSWORD(2002, "기존 비밀번호와 일치하지 않습니다"),
    PASSWORD_CHANGE_PASSWORD_MISMATCH(2003, "새로운 비밀번호와 확인 값이 일치하지 않습니다");


    private final Integer code;
    private final String message;
}

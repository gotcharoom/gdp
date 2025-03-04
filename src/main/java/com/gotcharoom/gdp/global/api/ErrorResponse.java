package com.gotcharoom.gdp.global.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorResponse {
    INTERNAL_SERVER_ERROR(9999, "API 호출 중 오류가 발생했습니다"),
    NOT_FOUND_END_POINT(9991, "요청한 API를 찾을 수 없습니다"),
    LOGIN_UNAUTHORIZED(1401, "인증되지 않은 로그인"),
    LOGIN_FORBIDDEN(1403, "권한 없음"),
    TEST_FAIL(2001, "Test 실패"),
    SAMPLE_ERROR(2002, "SAMPLEE RROR");

    private Integer code;
    private String message;
}

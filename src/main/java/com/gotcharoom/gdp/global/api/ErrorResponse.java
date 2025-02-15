package com.gotcharoom.gdp.global.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorResponse {
    TEST_FAIL(2001, "Test 실패");

    private Integer code;
    private String message;
}

package com.gotcharoom.gdp.global.exception.common;

import com.gotcharoom.gdp.global.api.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {
    private final ErrorResponse errorResponse;
}

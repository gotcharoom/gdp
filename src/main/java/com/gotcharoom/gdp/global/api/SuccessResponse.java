package com.gotcharoom.gdp.global.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessResponse {
    SUCCESS(1001, "Success");

    private Integer code;
    private String message;
}

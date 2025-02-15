package com.gotcharoom.gdp.global.api;

import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public class ApiResponse<T> implements Serializable {
    private Integer code;
    private String message;
    private T data;

    @Builder
    public ApiResponse(Integer code, String message, T data) {
        this.code = code != null ? code : SuccessResponse.SUCCESS.getCode();
        this.message = message != null ? message : SuccessResponse.SUCCESS.getMessage();
        this.data = data;
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(SuccessResponse.SUCCESS.getCode(), SuccessResponse.SUCCESS.getMessage(), null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(SuccessResponse.SUCCESS.getCode(), SuccessResponse.SUCCESS.getMessage(), data);
    }

    public static <T> ApiResponse<T> error(ErrorResponse errorResponse) {
        return new ApiResponse<>(errorResponse.getCode(), errorResponse.getMessage(), null);
    }

    public static <T> ApiResponse<T> error(ErrorResponse errorResponse, T data) {
        return new ApiResponse<>(errorResponse.getCode(), errorResponse.getMessage(), data);
    }
}

package com.gotcharoom.gdp.global.api;

import com.gotcharoom.gdp.global.exception.common.CustomException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class ApiResponse<T> implements Serializable {
    private Integer code;
    private String message;
    private T data;

    @Builder
    public ApiResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(SuccessResponse.SUCCESS.getCode(), SuccessResponse.SUCCESS.getMessage(), null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(SuccessResponse.SUCCESS.getCode(), SuccessResponse.SUCCESS.getMessage(), data);
    }

    public static <T> ApiResponse<T> error() {
        ErrorResponse errorResponse = ErrorResponse.INTERNAL_SERVER_ERROR;
        return new ApiResponse<>(errorResponse.getCode(), errorResponse.getMessage(), null);
    }

    public static <T> ApiResponse<T> error(ErrorResponse errorResponse) {
        return new ApiResponse<>(errorResponse.getCode(), errorResponse.getMessage(), null);
    }

    public static <T> ApiResponse<T> error(CustomException exception) {
        ErrorResponse errorResponse = exception.getErrorResponse();
        return new ApiResponse<>(errorResponse.getCode(), errorResponse.getMessage(), null);
    }
}

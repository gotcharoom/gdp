package com.gotcharoom.gdp.global.exception.custom;

import com.gotcharoom.gdp.global.api.ErrorResponse;
import com.gotcharoom.gdp.global.exception.common.CustomException;

public class ChangePasswordException extends CustomException {
    public ChangePasswordException(ErrorResponse errorResponse) {
        super(errorResponse);
    }
}

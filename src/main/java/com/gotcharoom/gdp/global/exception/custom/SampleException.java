package com.gotcharoom.gdp.global.exception.custom;

import com.gotcharoom.gdp.global.api.ErrorResponse;
import com.gotcharoom.gdp.global.exception.common.CustomException;

public class SampleException extends CustomException {
    public SampleException() {
        super(ErrorResponse.SAMPLE_ERROR);
    }
}

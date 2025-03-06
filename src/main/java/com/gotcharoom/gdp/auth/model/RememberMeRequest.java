package com.gotcharoom.gdp.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RememberMeRequest {
    private boolean rememberMe;
}

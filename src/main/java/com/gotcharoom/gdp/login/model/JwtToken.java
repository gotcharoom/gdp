package com.gotcharoom.gdp.login.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class JwtToken {
    private String accessToken;
    private String refreshToken;
}

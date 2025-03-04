package com.gotcharoom.gdp.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenEnum {
    AccessToken("access_token"),
    RefreshToken("refresh_token");

    private final String type;
}

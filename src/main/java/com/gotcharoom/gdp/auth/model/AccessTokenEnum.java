package com.gotcharoom.gdp.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccessTokenEnum {
    AccessToken("access_token");

    private final String type;
}

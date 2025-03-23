package com.gotcharoom.gdp.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CookieEnum {
    REMEMBER_ME("remember-me"),
    ACCESS_TOKEN("access_token"),
    REFRESH_TOKEN("refresh_token"),
    PLATFORM_CONNECTION("platform_connection");

    private final String type;
}

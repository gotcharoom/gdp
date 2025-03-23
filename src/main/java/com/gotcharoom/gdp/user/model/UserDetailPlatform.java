package com.gotcharoom.gdp.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserDetailPlatform {
    private Long userPlatformId;
    private Long platformId;
    private String platformName;
    private boolean connected;
    private String connectUrl;
    private String platformUserId;
    private String platformUserSecret;
}

package com.gotcharoom.gdp.platform.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlatformCreateRequest {
    private String name;
    private String url;
    private PlatformType platformType;
}

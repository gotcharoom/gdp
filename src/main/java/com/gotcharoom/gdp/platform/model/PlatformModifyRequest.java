package com.gotcharoom.gdp.platform.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlatformModifyRequest {
    private Long id;
    private String name;
    private String url;
    private PlatformUseYn useYn;
}

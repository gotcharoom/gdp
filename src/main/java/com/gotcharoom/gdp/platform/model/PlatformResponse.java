package com.gotcharoom.gdp.platform.model;

import com.gotcharoom.gdp.platform.entity.Platform;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlatformResponse {
    private Long id;
    private String name;
    private String url;
    private PlatformUseYn useYn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PlatformResponse fromEntity(Platform platform) {
        return PlatformResponse.builder()
                .id(platform.getId())
                .name(platform.getName())
                .url(platform.getUrl())
                .useYn(platform.getUseYn())
                .createdAt(platform.getCreatedAt())
                .updatedAt(platform.getUpdatedAt())
                .build();
    }
}

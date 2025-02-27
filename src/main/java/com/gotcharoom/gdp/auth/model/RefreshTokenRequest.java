package com.gotcharoom.gdp.auth.model;

import com.gotcharoom.gdp.auth.entity.RefreshToken;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RefreshTokenRequest {
    private String authName;
    private String refreshToken;
    private Long ttl;

    public RefreshToken toEntity() {
        return RefreshToken.builder()
                .authName(authName)
                .refreshToken(refreshToken)
                .ttl(ttl)
                .build();
    }
}

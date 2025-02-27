package com.gotcharoom.gdp.auth.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Builder
@RedisHash(value="refresh-token")
public class RefreshToken {

    @Id
    private String authName;

    private String refreshToken;

    // 토큰 만료
    private boolean expired;

    // 토큰 폐기
    private boolean revoked;

    @TimeToLive
    private Long ttl;
}

package com.gotcharoom.gdp.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

// TODO. [TR-YOO] timeToLive 수정하기
@Getter
@Builder
@AllArgsConstructor
@RedisHash(value="blacklisted-token", timeToLive = 3600)
public class BlacklistedToken {

    @Id
    private String token;
    private Long expirationTime;
}

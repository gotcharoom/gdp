package com.gotcharoom.gdp.auth.model;

import com.gotcharoom.gdp.user.entity.GdpUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserInfoResponse {
    private String id;
    private String nickname;
    private String name;

    public static LoginUserInfoResponse fromEntity(GdpUser gdpUser) {
        return LoginUserInfoResponse.builder()
                .id(gdpUser.getId())
                .nickname(gdpUser.getNickname())
                .name(gdpUser.getName())
                .build();
    }
}

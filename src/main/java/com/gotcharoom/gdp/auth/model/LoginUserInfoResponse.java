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
    private Long uid;
    private String id;
    private String nickName;
    private String name;

    public static LoginUserInfoResponse fromEntity(GdpUser gdpUser) {
        return LoginUserInfoResponse.builder()
                .uid(gdpUser.getUid())
                .id(gdpUser.getId())
                .nickName(gdpUser.getNickName())
                .name(gdpUser.getName())
                .build();
    }
}

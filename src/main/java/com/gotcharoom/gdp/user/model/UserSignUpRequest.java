package com.gotcharoom.gdp.user.model;

import com.gotcharoom.gdp.global.security.SocialType;
import com.gotcharoom.gdp.user.entity.GdpUser;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserSignUpRequest {
    private String id;
    private String nickName;
    private String name;
    private String password;

    public GdpUser toEntity() {

        return GdpUser.builder()
                .id(id)
                .nickName(nickName)
                .name(name)
                .password(password)
                .build();
    }

    public GdpUser toEntity(String encodedPassword) {

        return GdpUser.builder()
                .socialType(SocialType.GDP)
                .id(id)
                .socialId(null)
                .nickName(nickName)
                .name(name)
                .password(encodedPassword)
                .imageUrl(null)
                .build();
    }
}

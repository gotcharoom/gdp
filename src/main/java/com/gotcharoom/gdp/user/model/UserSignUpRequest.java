package com.gotcharoom.gdp.user.model;

import com.gotcharoom.gdp.global.security.Role;
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
    private String email;

    public GdpUser toEntity() {

        return GdpUser.builder()
                .socialType(SocialType.GDP)
                .id(id)
                .socialId(null)
                .nickName(nickName)
                .name(name)
                .password(password)
                .email(email)
                .role(Role.USER)
                .imageUrl(null)
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
                .email(email)
                .role(Role.USER)
                .imageUrl(null)
                .build();
    }
}

package com.gotcharoom.gdp.user.model;

import com.gotcharoom.gdp.global.security.model.Role;
import com.gotcharoom.gdp.global.security.model.SocialType;
import com.gotcharoom.gdp.user.entity.GdpUser;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserSignUpRequest {
    @NotNull
    private String id;
    @NotNull
    private String nickname;
    @NotNull
    private String name;
    @NotNull
    private String password;
    @NotNull
    private String email;

    public GdpUser toEntity() {

        return GdpUser.builder()
                .socialType(SocialType.GDP)
                .id(id)
                .socialId(null)
                .nickname(nickname)
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
                .nickname(nickname)
                .name(name)
                .password(encodedPassword)
                .email(email)
                .role(Role.USER)
                .imageUrl(null)
                .build();
    }
}

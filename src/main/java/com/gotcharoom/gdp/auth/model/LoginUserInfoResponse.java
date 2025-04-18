package com.gotcharoom.gdp.auth.model;

import com.gotcharoom.gdp.global.security.model.SocialType;
import com.gotcharoom.gdp.user.entity.GdpUser;
import com.gotcharoom.gdp.user.model.CropArea;
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
    private SocialType socialType;
    private String nickname;
    private String name;
    private String imageUrl;
    private CropArea imageCropArea;

    public static LoginUserInfoResponse fromEntity(GdpUser gdpUser) {
        return LoginUserInfoResponse.builder()
                .id(gdpUser.getId())
                .socialType(gdpUser.getSocialType())
                .nickname(gdpUser.getNickname())
                .name(gdpUser.getName())
                .imageUrl(gdpUser.getImageUrl())
                .imageCropArea(gdpUser.getCropArea())
                .build();
    }
}

package com.gotcharoom.gdp.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsResponse {
    private String id;
    private String email;
    private String name;
    private String nickname;
    private String imageUrl;
    private CropArea imageCropArea;
    private List<UserDetailPlatform> platforms;
    private Map<String, String> socials;
}

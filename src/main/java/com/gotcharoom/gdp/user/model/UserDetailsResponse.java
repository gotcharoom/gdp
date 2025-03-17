package com.gotcharoom.gdp.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsResponse {
    private String id;
    private String email;
    private String nickname;
    private Map<String, String> platforms;
    private Map<String, String> socials;
}

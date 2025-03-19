package com.gotcharoom.gdp.user.entity;

import com.gotcharoom.gdp.global.security.model.Role;
import com.gotcharoom.gdp.global.security.model.SocialType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor( access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "gdp_user")
public class GdpUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Enumerated(EnumType.STRING)
    @Column(name="social_type", nullable = false)
    private SocialType socialType;

    @Column(name="id", nullable = false, unique = true)
    private String id;

    @Column(name="social_id")
    private String socialId;

    @Column(name="nickname", nullable = false)
    private String nickname;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="password")
    private String password;

    @Column(name="email", nullable = false)
    private String email;

    @Column(name="image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable = false)
    private Role role;

    public GdpUser changePassword(String changedPassword) {
        return GdpUser.builder()
                .uid(uid)
                .socialType(socialType)
                .id(id)
                .socialId(socialId)
                .nickname(nickname)
                .name(name)
                .password(changedPassword)
                .email(email)
                .imageUrl(imageUrl)
                .role(role)
                .build();
    }
}

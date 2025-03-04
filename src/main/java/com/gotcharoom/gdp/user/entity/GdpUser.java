package com.gotcharoom.gdp.user.entity;

import com.gotcharoom.gdp.global.security.Role;
import com.gotcharoom.gdp.global.security.SocialType;
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
    @Column(name="social_type", nullable = false, unique = true)
    private SocialType socialType;

    @Column(name="id", nullable = false, unique = true)
    private String id;

    @Column(name="social_id")
    private String socialId;

    @Column(name="nick_name")
    private String nickName;

    @Column(name="name")
    private String name;

    @Column(name="password")
    private String password;

    @Column(name="email", nullable = false)
    private String email;

    @Column(name="image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name="role")
    private Role role;
}

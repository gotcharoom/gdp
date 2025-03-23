package com.gotcharoom.gdp.platform.entity;

import com.gotcharoom.gdp.user.entity.GdpUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_platform", uniqueConstraints = @UniqueConstraint(name = "uk_user_platform", columnNames = {"user_id", "platform_id"}))
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPlatform {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private GdpUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;

    @Column(name="platform_user_id", nullable = false)
    private String platformUserId;

    @Column(name="platform_user_secret")
    private String platformUserSecret;
}

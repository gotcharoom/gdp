package com.gotcharoom.gdp.user.entity;

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

    @Column(name="id", nullable = false, unique = true)
    private String id;

    @Column(name="nick_name", nullable = false)
    private String nickName;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="password", nullable = false)
    private String password;
}

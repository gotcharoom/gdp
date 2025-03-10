package com.gotcharoom.gdp.achievements.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(
        name = "user_achievement",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_user_achievement", columnNames = {
                        "steamId",
                        "game_name",
                        "apiname"
                })
        }
)
public class SteamAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 스팀 유저 id(코드) (비정규화로 처리)
    @Column(name="steam_id", nullable = false)
    private String steamID;

    // 게임 이름
    @Column(name="game_name", nullable = false)
    private String gameName;

    // 도전과제 id
    @Column(name="apiname", nullable = false)
    private String apiname;

    // 도전과제 달성 여부 (0=미달성, 1=달성)
    @Column(nullable = false)
    private byte achieved;

    // 해금 시간 (unix 시간)
    @Column(nullable = false)
    private Long unlocktime;

    // 도전과제 명칭
    @Column(name="name", nullable = false)
    private String name;

    // 도전과제 상세 설명
    @Column(nullable = false)
    private String description;

}

package com.gotcharoom.gdp.achievements.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(
        name = "user_display_stand_album_list",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_user_album", columnNames = {
                        "USER_DISPLAY_STAND_ID",
                        "USER_ALBUM_ID",
                })
        }
)
public class DisplayStandAlbumList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 전시대 테이블 id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_DISPLAY_STAND_ID")
    private UserDisplayStand userDisplayStand;

    // 앨범 id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ALBUM_ID")
    private UserAlbum userAlbum;

    // UserDisplayStand 연관 관계용 setter 메소드
    public void updateDisplayStand(UserDisplayStand newDisplayStand) {
        this.userDisplayStand = newDisplayStand;
    }
}

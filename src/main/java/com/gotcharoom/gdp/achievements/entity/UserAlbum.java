package com.gotcharoom.gdp.achievements.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "user_album")
public class UserAlbum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String contentText;

    private String image;

    private String userId;

    @OneToMany(mappedBy = "userAlbum", cascade = CascadeType.ALL)
    private List<UserAlbumAchievementList> achievements;

    // unix 시간으로 포맷
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "upload_date", updatable = false)
    @ColumnTransformer(read = "UNIX_TIMESTAMP(created_at)", write = "FROM_UNIXTIME(?)")
    private Date uploadDate;

}

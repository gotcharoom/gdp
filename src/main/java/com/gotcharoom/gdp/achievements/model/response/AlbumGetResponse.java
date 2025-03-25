package com.gotcharoom.gdp.achievements.model.response;

import com.gotcharoom.gdp.achievements.entity.UserSteamAchievement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlbumGetResponse {
    private Long id;
    private String title;
    private String contentText;
    private String image;
    List<UserSteamAchievement> achievements;
    private LocalDateTime create_date;
    private LocalDateTime update_date;
}

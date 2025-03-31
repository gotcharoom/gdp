package com.gotcharoom.gdp.achievements.model.request;

import com.gotcharoom.gdp.achievements.entity.UserAlbum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlbumSaveRequest {
    private Long displayStandId;

    private String title;

    private String contentText;

    private String image;

    // 연동한 도전과제 id값
    private List<Long> achievements;

    public UserAlbum toEntity(String userName, Long id) {
        return UserAlbum.builder()
                .id(id)
                .displayStandId(this.getDisplayStandId())
                .title(this.getTitle())
                .contentText(this.getContentText())
                .image(this.getImage())
                .userId(userName)
                .build();
    }


}

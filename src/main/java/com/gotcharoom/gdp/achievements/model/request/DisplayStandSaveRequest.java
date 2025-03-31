package com.gotcharoom.gdp.achievements.model.request;

import com.gotcharoom.gdp.achievements.entity.UserDisplayStand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DisplayStandSaveRequest {
    private String title;

    private String contentText;

    private String image;

    public UserDisplayStand toEntity(String userName, Long id) {
        return UserDisplayStand.builder()
                .id(id)
                .title(this.getTitle())
                .contentText(this.getContentText())
                .image(this.getImage())
                .userId(userName)
                .build();
    }
}

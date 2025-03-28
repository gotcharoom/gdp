package com.gotcharoom.gdp.achievements.model.response;

import com.gotcharoom.gdp.achievements.entity.UserAlbum;
import com.gotcharoom.gdp.achievements.entity.UserDisplayStand;
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
public class DisplayStandGetResponse {
    private Long id;
    private String title;
    private String contentText;
    private String image;
    List<UserAlbum> albums;
    private LocalDateTime create_date;
    private LocalDateTime update_date;

    public static DisplayStandGetResponse fromDisplayStandDetail(UserDisplayStand displayStand, List<UserAlbum> albumList) {
        return DisplayStandGetResponse.builder()
                .id(displayStand.getId())
                .title(displayStand.getTitle())
                .contentText(displayStand.getContentText())
                .image(displayStand.getImage())
                .albums(albumList)
                .create_date(displayStand.getCreatedAt())
                .update_date(displayStand.getUpdatedAt())
                .build();
    }
}

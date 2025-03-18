package com.gotcharoom.gdp.achievements.model.response;

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
public class DisplayStandGetResponse {
    private Long id;
    private String title;
    private String contentText;
    private String image;
    List<UserAlbum> albums;
}

package com.gotcharoom.gdp.achievements.model.request;

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
    private String title;

    private String contentText;

    private String image;

//    private String userId;

    // 연동한 도전과제 id값
    private List<Long> achievements;
}

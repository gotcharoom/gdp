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
public class DisplayStandSaveRequest {
    private String title;

    private String contentText;

    private String image;

//    private String userId;

    // 연동한 앨범 id값
    private List<Long> albums;
}

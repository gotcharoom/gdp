package com.gotcharoom.gdp.achievements.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DisplayStandGetListResponse {
    private Long id;
    private String title;
    private String image;
    private LocalDateTime uploadDate;
}

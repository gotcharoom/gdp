package com.gotcharoom.gdp.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CropArea {
    private int x;
    private int y;
    private int width;
    private int height;
}

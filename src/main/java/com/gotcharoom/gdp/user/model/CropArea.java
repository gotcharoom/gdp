package com.gotcharoom.gdp.user.model;

import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CropArea implements Serializable {
    private int x;
    private int y;
    private int width;
    private int height;
}

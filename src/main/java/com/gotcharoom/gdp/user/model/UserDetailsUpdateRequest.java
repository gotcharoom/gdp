package com.gotcharoom.gdp.user.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serializable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDetailsUpdateRequest implements Serializable {
    private String nickname;
    private String name;
    private String email;
    private MultipartFile imageFile;
    private CropArea imageCropArea;

    public void setImageCropArea(String imageCropAreaJson) {
        try {
            this.imageCropArea = new ObjectMapper().readValue(imageCropAreaJson, CropArea.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid JSON format for Address", e);
        }
    }
}

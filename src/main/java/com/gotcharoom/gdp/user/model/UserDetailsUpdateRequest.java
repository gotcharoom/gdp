package com.gotcharoom.gdp.user.model;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsUpdateRequest implements Serializable {
    private String nickname;
    private String name;
    private String email;
    private MultipartFile imageFile;
    private CropArea imageCropArea;
}

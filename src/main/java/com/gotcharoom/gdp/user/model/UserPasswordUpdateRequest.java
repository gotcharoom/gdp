package com.gotcharoom.gdp.user.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserPasswordUpdateRequest {
    @NotNull
    private String prevPassword;
    @NotNull
    private String newPassword;
    @NotNull
    private String newPasswordConfirm;
}

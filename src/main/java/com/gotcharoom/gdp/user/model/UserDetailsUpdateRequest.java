package com.gotcharoom.gdp.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsUpdateRequest {
    private String email;
    private String prevPassword;
    private String newPassword;
    private String newPasswordConfirm;

}

package com.gotcharoom.gdp.auth.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LoginRequest {
    @NotNull
    private String id;
    @NotNull
    private String password;
}

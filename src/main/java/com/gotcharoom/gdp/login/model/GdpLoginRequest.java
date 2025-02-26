package com.gotcharoom.gdp.login.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class GdpLoginRequest {
    @NotNull
    private String id;
    @NotNull
    private String password;
}

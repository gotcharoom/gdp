package com.gotcharoom.gdp.auth.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class GdpLoginRequest {
    @NotNull
    private String id;
    @NotNull
    private String password;
}

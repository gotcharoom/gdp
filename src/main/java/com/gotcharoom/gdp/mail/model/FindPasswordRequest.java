package com.gotcharoom.gdp.mail.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class FindPasswordRequest {
    @NotNull
    private String id;
    @NotNull
    private String email;
}

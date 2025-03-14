package com.gotcharoom.gdp.mail.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class FindIdRequest {
    @NotNull
    private String email;
}

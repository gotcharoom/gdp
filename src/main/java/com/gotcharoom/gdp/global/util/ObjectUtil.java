package com.gotcharoom.gdp.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.stereotype.Component;


@Getter
@Component
public class ObjectUtil {

    private final ObjectMapper objectMapper;

    public ObjectUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}


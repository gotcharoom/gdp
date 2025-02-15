package com.gotcharoom.gdp.sample.model;

import com.gotcharoom.gdp.sample.entity.SampleUser;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SampleModel {

    private Long id;
    private String name;
    private String password;

    public static SampleModel fromEntity(SampleUser sampleUser) {

        return SampleModel.builder()
                .id(sampleUser.getId())
                .name(sampleUser.getName())
                .password(sampleUser.getPassword())
                .build();
    }
}

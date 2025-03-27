package com.gotcharoom.gdp.elastic.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@NoArgsConstructor
public class TagDocument {
    @Field(type = FieldType.Keyword, index)
    private Long id;

    private String name;

    public TagDocument(String title) {

    }
}

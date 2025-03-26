package com.gotcharoom.gdp.elastic.entity;

import com.gotcharoom.gdp.elastic.model.BoardCategory;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@Document(indexName = "board")
@Setting(replicas = 0)
public class BoardDocument {
    @Id
    private String id;

    @Field(name = "category", type = FieldType.Keyword)
    private BoardCategory category;

    @Field(name = "title", type = FieldType.Text)
    private String title;

    @Field(name = "content", type = FieldType.Text)
    private String content;

    @Field(name = "is_private", type = FieldType.Boolean)
    private Boolean isPrivate;

    @Field(name= "owner_id", type = FieldType.Long)
    private Long ownerId;

    @Field(name= "is_deleted", type = FieldType.Boolean)
    private Boolean isDeleted;

    @Field(name = "created_at", type = FieldType.Date)
    private LocalDateTime createdAt;

    @Field(name = "updated_at", type = FieldType.Date)
    private LocalDateTime updatedAt;

    public static BoardDocument fromEntity() {
        return BoardDocument.builder().build();
    }
}

package com.gotcharoom.gdp.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    // API를 그룹별로 관리하면 됨
    @Bean
    public GroupedOpenApi sampleGroup() {
        // "/v1/**" 경로에 매칭되는 API를 그룹화하여 문서화한다.
        String[] paths = {"/sample"};

        return GroupedOpenApi.builder()
                .group("Sample")  // 그룹 이름을 설정한다.
                .pathsToMatch(paths)     // 그룹에 속하는 경로 패턴을 지정한다.
                .build();
    }

    @Bean
    public OpenAPI springOpenApi() {
        return new OpenAPI().info(new Info()
                .title("GDP 프로젝트")
                .description("도전과제 전시대 API Swagger")
                .version("v1")
        );
    }
}
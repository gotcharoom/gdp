package com.gotcharoom.gdp.elastic.controller;

import com.gotcharoom.gdp.elastic.service.ElasticService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/elastic")
@Tag(name = "Elasticsearch 연동", description = "Elasticsearch 연동 컨트롤러")
public class ElasticController {

    private final ElasticService elasticService;

    public ElasticController(ElasticService elasticService) {
        this.elasticService = elasticService;
    }
}

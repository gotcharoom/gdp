package com.gotcharoom.gdp.elastic.repository;

import com.gotcharoom.gdp.elastic.entity.BoardDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BoardElasticRepository extends ElasticsearchRepository<BoardDocument, String> {
}

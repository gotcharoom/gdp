package com.gotcharoom.gdp.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris}")
    private String[] ES_HOSTS;

    @Value("${spring.elasticsearch.username}")
    private String USER_NAME;

    @Value("${spring.elasticsearch.password}")
    private String PASSWORD;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(ES_HOSTS)
                .withBasicAuth(USER_NAME, PASSWORD)
                .build();
    }
}

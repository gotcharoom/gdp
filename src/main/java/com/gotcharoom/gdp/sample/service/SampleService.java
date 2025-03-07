package com.gotcharoom.gdp.sample.service;

import com.gotcharoom.gdp.global.util.WebClientUtil;
import com.gotcharoom.gdp.sample.entity.SampleUser;
import com.gotcharoom.gdp.sample.model.SampleModel;
import com.gotcharoom.gdp.sample.model.SampleCovidModel;
import com.gotcharoom.gdp.sample.repository.SampleUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class SampleService {

    @Value("${sample.covid.url}")
    private String COVID_API_URL;

    @Value("${sample.covid.api-key}")
    private String COVID_API_KEY;

    SampleUserRepository sampleUserRepository;
    WebClientUtil webClientUtil;

    SampleService(SampleUserRepository sampleUserRepository,
                  WebClientUtil webClientUtil) {
        this.sampleUserRepository = sampleUserRepository;
        this.webClientUtil = webClientUtil;
    }

    public SampleModel getSampleData() {
        SampleUser sampleUser = sampleUserRepository.findFirstBy();

        return SampleModel.fromEntity(sampleUser);
    }

    public SampleCovidModel getSampleCovid() {

        String url = UriComponentsBuilder.fromUriString(COVID_API_URL)
                .queryParam("serviceKey", COVID_API_KEY)
                .queryParam("page", 1)
                .queryParam("perPage", 10)
                .queryParam("returnType", "JSON")
                .build(true)
                .toUriString();

        return webClientUtil.get(url, SampleCovidModel.class);
    }

    public List<SampleCovidModel> getSampleCovidList() {

        String url = UriComponentsBuilder.fromUriString(COVID_API_URL)
                .queryParam("serviceKey", COVID_API_KEY)
                .queryParam("page", 1)
                .queryParam("perPage", 10)
                .queryParam("returnType", "JSON")
                .build(true)
                .toUriString();

        return webClientUtil.get(url, new ParameterizedTypeReference<List<SampleCovidModel>>() {});
    }
}

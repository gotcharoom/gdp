package com.gotcharoom.gdp.sample.service;

import com.gotcharoom.gdp.global.util.WebClientUtil;
import com.gotcharoom.gdp.sample.entity.SampleUser;
import com.gotcharoom.gdp.sample.model.SampleModel;
import com.gotcharoom.gdp.sample.repository.SampleUserRepository;
import org.springframework.stereotype.Service;

@Service
public class SampleService {

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

    public void getWebApiData() {

    }
}

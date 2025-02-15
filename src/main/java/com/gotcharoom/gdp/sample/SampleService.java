package com.gotcharoom.gdp.sample;

import com.gotcharoom.gdp.global.util.WebClientUtil;
import org.springframework.stereotype.Service;

@Service
public class SampleService {

    WebClientUtil webClientUtil;

    public void testMethod() {
        webClientUtil.get("", Void.class);
    }
}

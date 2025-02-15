package com.gotcharoom.gdp.sample.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SampleCovidModel {

    private Integer page;
    private Integer perPage;
    private List<SampleCovidData> data;
}

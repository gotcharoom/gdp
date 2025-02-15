package com.gotcharoom.gdp.sample.controller;

import com.gotcharoom.gdp.global.api.ApiResponse;
import com.gotcharoom.gdp.global.api.ErrorResponse;
import com.gotcharoom.gdp.sample.model.SampleModel;
import com.gotcharoom.gdp.sample.model.SampleCovidModel;
import com.gotcharoom.gdp.sample.service.SampleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


//@RequiredArgsConstructor
//@CrossOrigin
@RequestMapping("/sample")
@Tag(name = "Sample", description = "Sample API 입니다")
@RestController
public class SampleController {

    SampleService sampleService;

    SampleController(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    @Operation(
            summary = "테스트",
            description = "테스트합니다"
    )
//    @ApiResponse(
//            responseCode = "200",
//            description = "회원가입에 성공하였습니다."
//    )
    @PostMapping("/test")
    public void test() {
        System.out.println("test");
    }

    @Operation(
            summary = "Response 테스트 - 성공",
            description = "테스트합니다"
    )
    @PostMapping("/response/success")
    public ApiResponse<String> responseTestSuccess() {
        return ApiResponse.success();
    }

    @Operation(
            summary = "Response 테스트 - 성공 (데이터 포함)",
            description = "테스트합니다"
    )
    @GetMapping("/response/success/data")
    public ApiResponse<SampleModel> responseTestSuccessData() {
        SampleModel sampleModel = sampleService.getSampleData();

        return ApiResponse.success(sampleModel);
    }

    @Operation(
            summary = "Response 테스트 - 성공 (코로나 접종 장소 조회 API)",
            description = "테스트합니다"
    )
    @GetMapping("/response/success/covid")
    public ApiResponse<SampleCovidModel> responseTestSuccessWeather() {
        SampleCovidModel sampleCovidModel = sampleService.getSampleCovid();

        return ApiResponse.success(sampleCovidModel);
    }

    @Operation(
            summary = "테스트",
            description = "테스트합니다"
    )
    @GetMapping("/response/error")
    public ApiResponse<Void> responseTestError() {
        return ApiResponse.error(ErrorResponse.TEST_FAIL);
    }
}

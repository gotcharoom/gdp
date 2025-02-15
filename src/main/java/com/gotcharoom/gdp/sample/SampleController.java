package com.gotcharoom.gdp.sample;

import com.gotcharoom.gdp.global.api.ApiResponse;
import com.gotcharoom.gdp.global.api.ErrorResponse;
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
    public ApiResponse<String> responseTestSuccessData() {
        return ApiResponse.success("데이터");
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

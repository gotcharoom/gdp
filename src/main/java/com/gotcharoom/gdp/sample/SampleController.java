package com.gotcharoom.gdp.sample;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
}

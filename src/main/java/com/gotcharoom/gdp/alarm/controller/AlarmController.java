package com.gotcharoom.gdp.alarm.controller;

import com.gotcharoom.gdp.alarm.entity.AlarmTable;
import com.gotcharoom.gdp.alarm.repositoy.AlarmRepository;
import com.gotcharoom.gdp.alarm.service.AlarmService;
import com.gotcharoom.gdp.global.api.ApiResponse;
import com.gotcharoom.gdp.global.api.ErrorResponse;
import com.gotcharoom.gdp.sample.model.SampleCovidData;
import com.gotcharoom.gdp.sample.model.SampleModel;
import com.gotcharoom.gdp.sample.model.SampleCovidModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


//@RequiredArgsConstructor
//@CrossOrigin
@RequestMapping("/api/v1/alarm")
@Tag(name = "alarm", description = "alarm API 다.")
@RestController
public class AlarmController {

    AlarmService alarmService;

    AlarmController(AlarmService alarmService) {
        this.alarmService = alarmService;
    }

    @Operation(
            summary = "테스트",
            description = "테스트합니다"
    )
//    @ApiResponse(
//            responseCode = "200",
//            description = "회원가입에 성공하였습니다."
//    )
    @GetMapping("/test")
    public ApiResponse<String> test() {
        return ApiResponse.success();
    }

    @Operation(
            summary = "이게 제목인데 뭘봄",
            description = "테스트용도 내용임 ㅇㅋ?"
    )
    @PostMapping("/response/test")
    public ApiResponse<String> responseTestSuccess() {
        return ApiResponse.success();
    }



}

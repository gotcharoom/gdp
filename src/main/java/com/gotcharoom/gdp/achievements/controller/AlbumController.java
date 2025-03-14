package com.gotcharoom.gdp.achievements.controller;

import com.gotcharoom.gdp.achievements.model.request.AlbumSaveRequest;
import com.gotcharoom.gdp.achievements.service.AlbumService;
import com.gotcharoom.gdp.global.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/album")
@AllArgsConstructor
@Tag(name = "album", description = "앨범 관련 API")
@RestController
public class AlbumController {
    private final AlbumService albumService;

    @Operation(
            summary = "앨범 저장하기(신규)",
            description = "작성한 앨범 내용과 앨범에 등록한 도전과제를 저장 / return 타입 : 저장한 수(count)"
    )
    @PostMapping("/r1")
    public ApiResponse<Integer> saveAlbum(@RequestBody AlbumSaveRequest requestData) {

        // 리퀘스트 데이터 용 목데이터 추가
        AlbumSaveRequest mockData = albumService.albumRequestDataTest();
        return ApiResponse.success(albumService.saveUserAlbum(mockData));
    }

    @Operation(
            summary = "앨범 삭제",
            description = "선택한 앨범을 지우기 / return 타입 : String"
    )
    @PostMapping("/r2")
    public ApiResponse<String> deleteAlbum(@RequestBody long index) {
        albumService.deleteUserAlbum(index);
        return ApiResponse.success("ok");
    }

}

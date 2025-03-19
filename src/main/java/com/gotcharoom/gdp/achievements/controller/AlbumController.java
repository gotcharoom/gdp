package com.gotcharoom.gdp.achievements.controller;

import com.gotcharoom.gdp.achievements.model.request.AlbumSaveRequest;
import com.gotcharoom.gdp.achievements.model.response.AlbumGetListResponse;
import com.gotcharoom.gdp.achievements.model.response.AlbumGetResponse;
import com.gotcharoom.gdp.achievements.service.AlbumService;
import com.gotcharoom.gdp.global.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/displayStand/album")
@AllArgsConstructor
@Tag(name = "앨범 기능", description = "앨범 관련 API")
@RestController
public class AlbumController {
    private final AlbumService albumService;

    @Operation(
            summary = "앨범 저장하기",
            description = "작성한 앨범 내용과 앨범에 등록한 도전과제를 저장"
    )
    @PostMapping("/r1")
    public ApiResponse<String> saveAlbum(@RequestBody AlbumSaveRequest requestData) {
        albumService.saveUserAlbum(requestData);
        return ApiResponse.success("ok");
    }

    @Operation(
            summary = "앨범 수정하기",
            description = "수정한 변경 사항을 저장"
    )
    @PostMapping("/r2")
    public ApiResponse<String> editAlbum(@RequestBody AlbumSaveRequest requestData) {
        albumService.editUserAlbum(requestData);
        return ApiResponse.success("ok");
    }


    @Operation(
            summary = "앨범 삭제",
            description = "선택한 앨범을 지우기"
    )
    @PostMapping("/r3")
    public ApiResponse<String> deleteAlbum(@RequestBody long index) {
        albumService.deleteUserAlbum(index);
        return ApiResponse.success("ok");
    }

    @Operation(
            summary = "앨범 가져오기(1건)",
            description = "선택한 앨범 정보 가져오기"
    )
    @GetMapping("/r4")
    public ApiResponse<AlbumGetResponse> getAlbumDetail(@RequestParam("index") long index) {
        return ApiResponse.success(albumService.getUserAlbumOne(index));
    }

    @Operation(
            summary = "앨범 목록 가져오기",
            description = "앨범 정보 가져오기"
    )
    @GetMapping("/r5")
    public ApiResponse<PagedModel<AlbumGetListResponse>> getAlbumList(@RequestParam int pageNo) {
        return ApiResponse.success(new PagedModel<>(albumService.getUserAlbums(pageNo, 5)));
    }

    // --------------------------------------- TEST API ---------------------------------------

    @Operation(
            summary = "외부 api 테스트 (GetSchemaForGame)",
            description = "테스트 중"
    )
    @GetMapping("/test")
    public ApiResponse<Object> test() {
        return ApiResponse.success(albumService.test());
    }


}

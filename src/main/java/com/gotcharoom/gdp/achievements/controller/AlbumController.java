package com.gotcharoom.gdp.achievements.controller;

import com.gotcharoom.gdp.achievements.entity.UserAlbum;
import com.gotcharoom.gdp.achievements.model.request.AlbumSaveRequest;
import com.gotcharoom.gdp.achievements.model.response.AlbumGetResponse;
import com.gotcharoom.gdp.achievements.service.AlbumService;
import com.gotcharoom.gdp.global.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/album")
@AllArgsConstructor
@Tag(name = "앨범 기능", description = "앨범 관련 API")
@RestController
public class AlbumController {
    private final AlbumService albumService;

    @Operation(
            summary = "앨범 저장/수정하기",
            description = "작성한 앨범 내용과 앨범에 등록한 도전과제를 저장 / return 타입 : 저장한 수(count)"
    )
    @PostMapping("/r1")
    public ApiResponse<Integer> saveAlbum(@RequestBody AlbumSaveRequest requestData) {

        // 리퀘스트 데이터 용 목데이터 추가
//        AlbumSaveRequest mockData = albumService.albumRequestDataTest(requestData.getAchievements());
        return ApiResponse.success(albumService.saveUserAlbum(requestData));
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

    @Operation(
            summary = "앨범 가져오기(1건)",
            description = "선택한 앨범 정보 가져오기 / return 타입 : UserAlbum"
    )
    @GetMapping("/r3")
    public ApiResponse<AlbumGetResponse> getAlbumDetail(@RequestParam("index") long index) {
        return ApiResponse.success(albumService.getUserAlbumOne(index));
    }

    @Operation(
            summary = "앨범 목록 가져오기",
            description = "앨범 정보 가져오기 / return 타입 : "
    )
    @GetMapping("/r4")
    public ApiResponse<Page<UserAlbum>> getAlbumList() {
        return ApiResponse.success(albumService.getUserAlbums(1, 2));
    }


}

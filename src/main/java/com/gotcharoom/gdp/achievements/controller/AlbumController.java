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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    @PostMapping
    public ApiResponse<String> saveAlbum(@AuthenticationPrincipal UserDetails userDetails,
                                         @RequestBody AlbumSaveRequest requestData) {
        albumService.saveUserAlbum(userDetails.getUsername(), requestData);
        return ApiResponse.success("ok");
    }

    @Operation(
            summary = "앨범 수정하기",
            description = "수정한 변경 사항을 저장"
    )
    @PutMapping("/{id}")
    public ApiResponse<String> editAlbum(@AuthenticationPrincipal UserDetails userDetails,
                                         @PathVariable Long id,
                                         @RequestBody AlbumSaveRequest requestData) {
        albumService.editUserAlbum(userDetails.getUsername(), id, requestData);
        return ApiResponse.success("ok");
    }


    @Operation(
            summary = "앨범 삭제",
            description = "선택한 앨범을 지우기"
    )
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteAlbum(@AuthenticationPrincipal UserDetails userDetails,
                                           @PathVariable Long id) {
        albumService.deleteUserAlbum(userDetails.getUsername(), id);
        return ApiResponse.success("ok");
    }

    @Operation(
            summary = "앨범 가져오기(1건 상세보기)",
            description = "선택한 앨범 정보 가져오기"
    )
    @GetMapping("/detail")
    public ApiResponse<AlbumGetResponse> getAlbumDetail(@RequestParam("id") Long id) {
        return ApiResponse.success(albumService.getUserAlbumOne(id));
    }

    @Operation(
            summary = "앨범 목록 가져오기",
            description = "앨범 정보 가져오기"
    )
    @GetMapping
    public ApiResponse<PagedModel<AlbumGetListResponse>> getAlbumList(
            @RequestParam("page_no") int pageNo,
            @RequestParam(value = "page_size", required = false, defaultValue = "10") int pageSize) {
        return ApiResponse.success(new PagedModel<>(albumService.getUserAlbums(pageNo, pageSize)));
    }

    // --------------------------------------- TEST API ---------------------------------------

    @Operation(
            summary = "테스트",
            description = "테스트 중"
    )
    @GetMapping("/test")
    public ApiResponse<Object> test() {
        return ApiResponse.success(albumService.test());
    }


}

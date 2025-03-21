package com.gotcharoom.gdp.achievements.repository;

import com.gotcharoom.gdp.achievements.entity.UserAlbum;

import java.util.List;

public interface CustomDisplayStandAlbumListRepository {
    List<UserAlbum> findAlbumList(Long albumId);
}

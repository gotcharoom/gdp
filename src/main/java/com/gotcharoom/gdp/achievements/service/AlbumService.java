package com.gotcharoom.gdp.achievements.service;

import com.gotcharoom.gdp.achievements.entity.UserAlbum;
import com.gotcharoom.gdp.achievements.repository.SteamAchievmentRepository;
import com.gotcharoom.gdp.achievements.repository.UserAlbumRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AlbumService {

    private final SteamAchievmentRepository steamAchievmentRepository;
    private final UserAlbumRepository userAlbumRepository;
    public AlbumService(SteamAchievmentRepository steamAchievmentRepository, UserAlbumRepository userAlbumRepository) {
        this.steamAchievmentRepository = steamAchievmentRepository;
        this.userAlbumRepository = userAlbumRepository;
    }

    public Optional<UserAlbum> getUserAlbum() {
        return userAlbumRepository.findById(1L);
    }
}

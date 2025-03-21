package com.gotcharoom.gdp.achievements.repository;

import com.gotcharoom.gdp.achievements.entity.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CustomDisplayStandAlbumListRepositoryImpl implements CustomDisplayStandAlbumListRepository {
    private final JPAQueryFactory jpaQueryFactory;

    private final QDisplayStandAlbumList displayStandAlbumList = QDisplayStandAlbumList.displayStandAlbumList;
    private final QUserDisplayStand userDisplayStand = QUserDisplayStand.userDisplayStand;
    private final QUserAlbum userAlbum = QUserAlbum.userAlbum;

    @Override
    public List<UserAlbum> findAlbumList(Long displayStandId) {
        return jpaQueryFactory
                .select(userAlbum)
                .from(displayStandAlbumList)
                .join(displayStandAlbumList.userDisplayStand, userDisplayStand)
                .join(displayStandAlbumList.userAlbum, userAlbum)
                .where(displayStandAlbumList.userDisplayStand.id.eq(displayStandId))
                .fetch();

    }
}

package com.gotcharoom.gdp.achievements.repository;

import com.gotcharoom.gdp.achievements.entity.QUserAlbum;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

public class CustomUserAlbumRepositoryImpl implements CustomUserAlbumRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public CustomUserAlbumRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<String> title() {
        QUserAlbum album = QUserAlbum.userAlbum;
        return jpaQueryFactory.select(album.title).from(album).fetch();
    }
}

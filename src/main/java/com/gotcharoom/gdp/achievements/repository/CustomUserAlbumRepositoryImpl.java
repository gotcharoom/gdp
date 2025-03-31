package com.gotcharoom.gdp.achievements.repository;

import com.gotcharoom.gdp.achievements.entity.QUserAlbum;
import com.gotcharoom.gdp.achievements.model.response.AlbumGetListResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;


@RequiredArgsConstructor
public class CustomUserAlbumRepositoryImpl implements CustomUserAlbumRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final QUserAlbum album = QUserAlbum.userAlbum;

    @Override
    public Page<AlbumGetListResponse> findPageBy(Long displayStandId, Pageable page) {

        List<AlbumGetListResponse> results = jpaQueryFactory
                .select(Projections.constructor(
                        AlbumGetListResponse.class,
                        album.id,
                        album.displayStandId,
                        album.title,
                        album.image,
                        album.createdAt,
                        album.updatedAt
                ))
                .from(album)
                .where(album.displayStandId.eq(displayStandId))
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .orderBy(album.id.desc())
                .fetch();

        JPAQuery<Long> total = jpaQueryFactory
                .select(album.count())
                .where(album.displayStandId.eq(displayStandId))
                .from(album);

        // NullPointerException주의 사항 무시 (null로 들어갈 일이 없음)
        return PageableExecutionUtils.getPage(results, page, () -> total.fetchOne());

    }

    @Override
    public Page<AlbumGetListResponse> findAllByTitleContains(String title, Pageable page) {

        List<AlbumGetListResponse> results = jpaQueryFactory
                .select(Projections.constructor(
                        AlbumGetListResponse.class,
                        album.id,
                        album.displayStandId,
                        album.title,
                        album.image,
                        album.createdAt,
                        album.updatedAt
                ))
                .from(album)
                .where(album.title.contains(title))
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .orderBy(album.id.desc())
                .fetch();

        JPAQuery<Long> total = jpaQueryFactory
                .select(album.count())
                .from(album);

        // NullPointerException주의 사항 무시 (null로 들어갈 일이 없음)
        return PageableExecutionUtils.getPage(results, page, () -> total.fetchOne());
    }

}

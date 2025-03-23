package com.gotcharoom.gdp.platform.repository;

import com.gotcharoom.gdp.platform.entity.QPlatform;
import com.gotcharoom.gdp.platform.entity.QUserPlatform;
import com.gotcharoom.gdp.platform.model.PlatformUseYn;
import com.gotcharoom.gdp.user.model.UserDetailPlatform;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Optional;

public class CustomUserPlatformRepositoryImpl implements CustomUserPlatformRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public CustomUserPlatformRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }


    @Override
    public Optional<List<UserDetailPlatform>> findActiveUserPlatformsByUserId(Long uid) {
        QPlatform qPlatform = QPlatform.platform;
        QUserPlatform qUserPlatform = QUserPlatform.userPlatform;

        /*
        * 조건
        * 1. 현재 사용 중인 Platform 목록을 모두 출력하면서
        * 2. user의 uid를 통해 UserPlatform을 조회하여
        * 3. 사용 중인 Platform 정보와 한께 전달 (없을 경우 Platform 정보만 전달)
        */
        List<UserDetailPlatform> result = jpaQueryFactory
                .select(
                        Projections.constructor(
                                UserDetailPlatform.class,
                                qUserPlatform.id,
                                qPlatform.id,
                                qPlatform.name,
                                new CaseBuilder()
                                        .when(qUserPlatform.id.isNotNull()).then(true)
                                        .otherwise(false),
                                qPlatform.url,
                                qUserPlatform.platformUserId,
                                qUserPlatform.platformUserSecret
                        )
                )
                .from(qPlatform)
                .leftJoin(qUserPlatform).on(
                        qUserPlatform.platform.id.eq(qPlatform.id)
                        .and(qUserPlatform.user.uid.eq(uid))
                )
                .where(qPlatform.useYn.eq(PlatformUseYn.Y))
                .fetch();

        return Optional.of(result);
    }
}

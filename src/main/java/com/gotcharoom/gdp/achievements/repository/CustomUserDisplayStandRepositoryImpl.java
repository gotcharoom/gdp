package com.gotcharoom.gdp.achievements.repository;

import com.gotcharoom.gdp.achievements.entity.QUserDisplayStand;
import com.gotcharoom.gdp.achievements.model.response.DisplayStandGetListResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class CustomUserDisplayStandRepositoryImpl implements CustomUserDisplayStandRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final QUserDisplayStand displayStand = QUserDisplayStand.userDisplayStand;

    @Override
    public Page<DisplayStandGetListResponse> findPageBy(Pageable page) {
        List<DisplayStandGetListResponse> results = jpaQueryFactory
                .select(Projections.constructor(
                        DisplayStandGetListResponse.class,
                        displayStand.id,
                        displayStand.title,
                        displayStand.image,
                        displayStand.createdAt,
                        displayStand.updatedAt
                ))
                .from(displayStand)
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .orderBy(displayStand.id.desc())
                .fetch();

        JPAQuery<Long> total = jpaQueryFactory
                .select(displayStand.count())
                .from(displayStand);

        // NullPointerException주의 사항 무시 (null로 들어갈 일이 없음)
        return PageableExecutionUtils.getPage(results, page, () -> total.fetchOne());
    }
}

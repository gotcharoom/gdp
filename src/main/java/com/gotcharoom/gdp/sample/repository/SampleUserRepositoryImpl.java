package com.gotcharoom.gdp.sample.repository;

import com.gotcharoom.gdp.user.entity.QGdpUser;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

public class SampleUserRepositoryImpl implements SampleUserCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public SampleUserRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<String> myCustom() {
        QGdpUser qGdpUser = QGdpUser.gdpUser;

        jpaQueryFactory.select(qGdpUser.id)
                .from(qGdpUser)
                .fetch();

        return null;
    }
}

package com.trippia.travel.domain.companionpost.post;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.trippia.travel.controller.dto.post.request.CursorData;
import com.trippia.travel.controller.dto.post.request.PostSearchCondition;
import com.trippia.travel.domain.companionpost.comment.QCompanionPostComment;
import com.trippia.travel.domain.location.city.QCity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class CompanionPostRepositoryCustomImpl implements CompanionPostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<CompanionPost> searchDiariesWithConditions(PostSearchCondition condition, CursorData cursorData, Pageable pageable) {
        QCompanionPost post = QCompanionPost.companionPost;
        QCity city = QCity.city;
        QCompanionPostComment comment = QCompanionPostComment.companionPostComment;

        int pageSize = pageable.getPageSize();

        Sort.Order sortOrder = pageable.getSort().iterator().hasNext()
                ? pageable.getSort().iterator().next()
                : Sort.Order.desc("createdAt");

        Order direction = sortOrder.isAscending() ? Order.ASC : Order.DESC;
        String sortProperty = sortOrder.getProperty();

        List<CompanionPost> content = queryFactory
                .selectFrom(post)
                .distinct()
                .leftJoin(post.city, city).fetchJoin()
                .leftJoin(post.comments, comment)
                .where(
                        containsKeyword(condition.getKeyword(), post),
                        eqCityName(condition.getCityName(), city),
                        eqCountryName(condition.getCountryName(), city),
                        cursorPredicate(sortProperty, direction, cursorData, post)
                )
                .orderBy(
                        new OrderSpecifier(direction, new PathBuilder<>(CompanionPost.class, "companionPost").get(sortProperty)),
                        new OrderSpecifier(direction, post.id)
                )
                .limit(pageSize + 1)
                .fetch();

        boolean hasNext = content.size() > pageSize;
        if (hasNext) {
            content.remove(pageSize);
        }

        log.info("[쿼리 결과] 조회된 개수: {}, hasNext: {}", content.size(), hasNext);

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private BooleanExpression cursorPredicate(String property, Order direction, CursorData cursorData, QCompanionPost post) {
        if (property.equals("createdAt") && cursorData.getLastCreatedAt() != null) {
            return (direction == Order.ASC)
                    ? post.createdAt.gt(cursorData.getLastCreatedAt())
                    .or(post.createdAt.eq(cursorData.getLastCreatedAt())
                            .and(post.id.gt(cursorData.getLastId())))
                    : post.createdAt.lt(cursorData.getLastCreatedAt())
                    .or(post.createdAt.eq(cursorData.getLastCreatedAt())
                            .and(post.id.lt(cursorData.getLastId())));
        }

        if (property.equals("viewCount") && cursorData.getLastViewCount() != null) {
            return (direction == Order.ASC)
                    ? post.viewCount.gt(cursorData.getLastViewCount())
                    .or(post.viewCount.eq(cursorData.getLastViewCount())
                            .and(post.id.gt(cursorData.getLastId())))
                    : post.viewCount.lt(cursorData.getLastViewCount())
                    .or(post.viewCount.eq(cursorData.getLastViewCount())
                            .and(post.id.lt(cursorData.getLastId())));
        }

        return null;
    }

    private BooleanExpression containsKeyword(String keyword, QCompanionPost post) {
        return keyword != null ?
                post.title.containsIgnoreCase(keyword)
                        .or(post.content.containsIgnoreCase(keyword)) : null;
    }

    private BooleanExpression eqCityName(String cityName, QCity city) {
        return cityName != null ? city.name.eq(cityName) : null;
    }

    private BooleanExpression eqCountryName(String countryName, QCity city) {
        return countryName != null ? city.country.name.eq(countryName) : null;
    }
}
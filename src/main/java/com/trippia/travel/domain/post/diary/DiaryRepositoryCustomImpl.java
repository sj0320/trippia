package com.trippia.travel.domain.post.diary;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.trippia.travel.controller.dto.CursorData;
import com.trippia.travel.controller.dto.diary.request.DiarySearchCondition;
import com.trippia.travel.domain.location.city.QCity;
import com.trippia.travel.domain.post.diarytheme.QDiaryTheme;
import com.trippia.travel.domain.theme.QTheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
@Slf4j
public class DiaryRepositoryCustomImpl implements DiaryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Diary> searchDiariesWithConditions(DiarySearchCondition condition, CursorData cursorData, Pageable pageable) {
        QDiary diary = QDiary.diary;
        QTheme themeEntity = QTheme.theme;
        QDiaryTheme diaryThemeEntity = QDiaryTheme.diaryTheme;
        QCity cityEntity = QCity.city;

        int pageSize = pageable.getPageSize();

        Sort.Order sortOrder = pageable.getSort().iterator().hasNext() ?
                pageable.getSort().iterator().next() :
                Sort.Order.desc("createdAt");

        Order direction = sortOrder.isAscending() ? Order.ASC : Order.DESC;
        String sortProperty = sortOrder.getProperty();
        List<Diary> content = queryFactory
                .selectFrom(diary)
                .distinct()
                .leftJoin(diary.city, cityEntity).fetchJoin()
                .leftJoin(diaryThemeEntity).on(diary.eq(diaryThemeEntity.diary))
                .leftJoin(diaryThemeEntity.theme, themeEntity)
                .where(
                        eqThemeName(condition.getThemeName(), themeEntity),
                        eqCountryName(condition.getCountryName(), cityEntity),
                        eqCityName(condition.getCityName(), cityEntity),
                        containsKeyword(condition.getKeyword(), diary),
                        cursorPredicate(sortProperty, direction, cursorData, diary)
                )
                .orderBy(
                        new OrderSpecifier(direction, new PathBuilder<>(Diary.class, "diary").get(sortProperty)),
                        new OrderSpecifier(direction, diary.id) // tie-breaker
                )
                .limit(pageSize + 1)
                .fetch();

        boolean hasNext = content.size() > pageSize;
        if (hasNext) {
            content.remove(pageSize);
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private BooleanExpression cursorPredicate(String property, Order direction, CursorData cursorData, QDiary diary) {
        if (property.equals("createdAt") && cursorData.getLastCreatedAt() != null) {
            if (direction == Order.ASC) {
                return diary.createdAt.gt(cursorData.getLastCreatedAt())
                        .or(diary.createdAt.eq(cursorData.getLastCreatedAt())
                                .and(diary.id.gt(cursorData.getLastId())));
            } else {
                return diary.createdAt.lt(cursorData.getLastCreatedAt())
                        .or(diary.createdAt.eq(cursorData.getLastCreatedAt())
                                .and(diary.id.lt(cursorData.getLastId())));
            }
        }

        if (property.equals("likeCount") && cursorData.getLastLikeCount() != null) {
            if (direction == Order.ASC) {
                return diary.likeCount.gt(cursorData.getLastLikeCount())
                        .or(diary.likeCount.eq(cursorData.getLastLikeCount())
                                .and(diary.id.gt(cursorData.getLastId())));
            } else {
                return diary.likeCount.lt(cursorData.getLastLikeCount())
                        .or(diary.likeCount.eq(cursorData.getLastLikeCount())
                                .and(diary.id.lt(cursorData.getLastId())));
            }
        }

        if (property.equals("viewCount") && cursorData.getLastViewCount() != null) {
            if (direction == Order.ASC) {
                return diary.viewCount.gt(cursorData.getLastViewCount())
                        .or(diary.viewCount.eq(cursorData.getLastViewCount())
                                .and(diary.id.gt(cursorData.getLastId())));
            } else {
                return diary.viewCount.lt(cursorData.getLastViewCount())
                        .or(diary.viewCount.eq(cursorData.getLastViewCount())
                                .and(diary.id.lt(cursorData.getLastId())));
            }
        }

        return null;
    }

    private BooleanExpression eqThemeName(String theme, QTheme themeEntity) {
        log.info("theme조건 ={}", theme);
        return theme != null ? themeEntity.name.eq(theme) : null;
    }

    private BooleanExpression eqCountryName(String country, QCity cityEntity) {
        log.info("country조건 = {}", country);
        return country != null ? cityEntity.country.name.eq(country) : null;
    }

    private BooleanExpression eqCityName(String city, QCity cityEntity) {
        log.info("city조건 = {}", city);
        return city != null ? cityEntity.name.eq(city) : null;
    }


    private BooleanExpression containsKeyword(String keyword, QDiary diary) {
        return keyword != null ?
                diary.title.containsIgnoreCase(keyword)
                        .or(diary.content.containsIgnoreCase(keyword)) : null;
    }

}

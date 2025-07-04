package com.trippia.travel.domain.diarypost.diary;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.trippia.travel.controller.dto.diary.request.CursorData;
import com.trippia.travel.controller.dto.diary.request.DiarySearchCondition;
import com.trippia.travel.domain.diarypost.diarytheme.QDiaryTheme;
import com.trippia.travel.domain.location.city.QCity;
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
        QDiaryTheme diaryTheme = QDiaryTheme.diaryTheme;
        QCity city = QCity.city;

        int pageSize = pageable.getPageSize();

        Sort.Order sortOrder = pageable.getSort().iterator().hasNext() ?
                pageable.getSort().iterator().next() :
                Sort.Order.desc("createdAt");

        Order direction = sortOrder.isAscending() ? Order.ASC : Order.DESC;
        String sortProperty = sortOrder.getProperty();

        // Step 1: diary_id 리스트만 페이징 + 정렬 조건으로 조회
        List<Long> diaryIds = queryFactory
                .select(diary.id)
                .from(diary)
                .leftJoin(diaryTheme).on(diaryTheme.diary.eq(diary))
                .where(
                        eqThemeId(condition.getThemeId(), diaryTheme),
                        eqCountryId(condition.getCountryId(), city),
                        eqCityName(condition.getCityName(), city),
                        containsKeyword(condition.getKeyword(), diary),
                        cursorPredicate(sortProperty, direction, cursorData, diary)
                )
                .orderBy(
                        new OrderSpecifier(direction, new PathBuilder<>(Diary.class, "diary").get(sortProperty)),
                        new OrderSpecifier(direction, diary.id)
                )
                .limit(pageSize * 3L)
                .fetch();

        if (diaryIds.isEmpty()) {
            return new SliceImpl<>(List.of(), pageable, false);
        }

        // Step 2: id 리스트 기반으로 fetch join 수행
        List<Diary> content = queryFactory
                .selectFrom(diary)
                .distinct()
                .where(diary.id.in(diaryIds))
                .orderBy(
                        new OrderSpecifier(direction, new PathBuilder<>(Diary.class, "diary").get(sortProperty)),
                        new OrderSpecifier(direction, diary.id)
                )
                .limit(pageSize)
                .fetch();

        boolean hasNext = content.size() > pageSize;
        if (hasNext) {
            content.remove(pageSize);
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private BooleanExpression cursorPredicate(String property, Order direction, CursorData cursorData, QDiary diary) {
        if (property.equals("createdAt") && cursorData.getLastCreatedAt() != null) {
            return direction == Order.ASC ?
                    diary.createdAt.gt(cursorData.getLastCreatedAt())
                            .or(diary.createdAt.eq(cursorData.getLastCreatedAt()).and(diary.id.gt(cursorData.getLastId()))) :
                    diary.createdAt.lt(cursorData.getLastCreatedAt())
                            .or(diary.createdAt.eq(cursorData.getLastCreatedAt()).and(diary.id.lt(cursorData.getLastId())));
        }

        if (property.equals("likeCount") && cursorData.getLastLikeCount() != null) {
            return direction == Order.ASC ?
                    diary.likeCount.gt(cursorData.getLastLikeCount())
                            .or(diary.likeCount.eq(cursorData.getLastLikeCount()).and(diary.id.gt(cursorData.getLastId()))) :
                    diary.likeCount.lt(cursorData.getLastLikeCount())
                            .or(diary.likeCount.eq(cursorData.getLastLikeCount()).and(diary.id.lt(cursorData.getLastId())));
        }

        if (property.equals("viewCount") && cursorData.getLastViewCount() != null) {
            return direction == Order.ASC ?
                    diary.viewCount.gt(cursorData.getLastViewCount())
                            .or(diary.viewCount.eq(cursorData.getLastViewCount()).and(diary.id.gt(cursorData.getLastId()))) :
                    diary.viewCount.lt(cursorData.getLastViewCount())
                            .or(diary.viewCount.eq(cursorData.getLastViewCount()).and(diary.id.lt(cursorData.getLastId())));
        }

        return null;
    }

    private BooleanExpression eqThemeId(Long themeId, QDiaryTheme diaryTheme) {
        return themeId != null ? diaryTheme.theme.id.eq(themeId) : null;
    }

    private BooleanExpression eqCountryId(Long countryId, QCity city) {
        return countryId != null ? city.country.id.eq(countryId) : null;
    }

    private BooleanExpression eqCityName(String city, QCity cityEntity) {
        return city != null ? cityEntity.name.eq(city) : null;
    }

    private BooleanExpression containsKeyword(String keyword, QDiary diary) {
        return keyword != null ?
                diary.title.containsIgnoreCase(keyword)
                        .or(diary.content.containsIgnoreCase(keyword)) : null;
    }
}
package com.trippia.travel.domain.diarypost.likes;

import com.trippia.travel.controller.dto.diary.response.LikedDiarySummaryResponse;
import com.trippia.travel.domain.notification.NotificationService;
import com.trippia.travel.domain.notification.dto.LikeNotificationDto;
import com.trippia.travel.domain.diarypost.diary.Diary;
import com.trippia.travel.domain.diarypost.diary.DiaryRepository;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.event.diary.model.DiaryLikedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public int likeDiary(String email, Long diaryId) {
        User user = getUser(email);
        Diary diary = getDiary(diaryId);

        if (!likeRepository.existsByUserAndDiary(user, diary)) {
            Likes likes = getDiaryUserLikes(diary, user);
            likeRepository.save(likes);

            int newLikeCount = diary.addLike();

            if (!user.equals(diary.getUser())) {
                LikeNotificationDto notification = LikeNotificationDto.builder()
                        .user(diary.getUser())
                        .likerNickname(user.getNickname())
                        .diaryId(diaryId)
                        .build();
                notificationService.sendNotification(notification);
            }
            eventPublisher.publishEvent(new DiaryLikedEvent(diaryId, newLikeCount));
            return newLikeCount;
        }

        return diary.getLikeCount();
    }

    @Transactional
    public int unlikeDiary(String email, Long diaryId) {
        User user = getUser(email);
        Diary diary = getDiary(diaryId);
        if (likeRepository.existsByUserAndDiary(user, diary)) {
            likeRepository.deleteByUserAndDiary(user, diary);
            return diary.cancelLike();
        }
        return diary.getLikeCount();
    }

    public boolean isLikedByDiary(String email, Long diaryId) {
        User user = userRepository.findByEmail(email).orElse(null);
        Diary diary = getDiary(diaryId);
        return likeRepository.existsByUserAndDiary(user, diary);
    }

    public List<LikedDiarySummaryResponse> getLikedDiariesByUser(String email) {
        User user = getUser(email);
        List<Likes> likes = likeRepository.findAllByUserId(user.getId());

        return likes.stream()
                .map(like -> LikedDiarySummaryResponse.from(like.getDiary()))
                .toList();
    }

    private Likes getDiaryUserLikes(Diary diary, User user) {
        return Likes.builder()
                .diary(diary)
                .user(user)
                .build();
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    private Diary getDiary(Long diaryId) {
        return diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("여행일지 데이터를 찾을 수 없습니다."));
    }


}

package com.trippia.travel.domain.post.likes;

import com.trippia.travel.domain.post.diary.Diary;
import com.trippia.travel.domain.post.diary.DiaryRepository;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;

    @Transactional
    public int likeDiary(String email, Long diaryId) {
        User user = getUserByEmail(email);
        Diary diary = getDiary(diaryId);
        if (!likeRepository.existsByUserAndDiary(user, diary)) {
            Likes likes = getDiaryUserLikes(diary, user);
            likeRepository.save(likes);
            return diary.addLike();
        }
        return diary.getLikeCount();
    }

    @Transactional
    public int unlikeDiary(String email, Long diaryId) {
        User user = getUserByEmail(email);
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

    private static Likes getDiaryUserLikes(Diary diary, User user) {
        return Likes.builder()
                .diary(diary)
                .user(user)
                .build();
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    private Diary getDiary(Long diaryId) {
        return diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("여행일지 데이터를 찾을 수 없습니다."));
    }

}

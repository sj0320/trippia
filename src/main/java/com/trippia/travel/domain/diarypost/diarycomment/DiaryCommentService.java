package com.trippia.travel.domain.diarypost.diarycomment;

import com.trippia.travel.controller.dto.diarycomment.requset.DiaryCommentSaveRequest;
import com.trippia.travel.controller.dto.diarycomment.response.DiaryCommentResponse;
import com.trippia.travel.domain.notification.NotificationService;
import com.trippia.travel.domain.notification.dto.DiaryCommentNotificationDto;
import com.trippia.travel.domain.diarypost.diary.Diary;
import com.trippia.travel.domain.diarypost.diary.DiaryRepository;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.exception.diary.DiaryException;
import com.trippia.travel.exception.user.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryCommentService {

    private final DiaryCommentRepository diaryCommentRepository;
    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public DiaryCommentResponse saveDiaryComment(String email, Long diaryId, DiaryCommentSaveRequest request) {
        Diary diary = getDiary(diaryId);
        User user = getUserByEmail(email);
        String content = request.getContent();
        DiaryComment diaryComment = DiaryComment.createComment(user, diary, content);

        diary.addComment(diaryComment);
        diaryCommentRepository.save(diaryComment);
        if (!user.equals(diary.getUser())) {
            DiaryCommentNotificationDto notification = DiaryCommentNotificationDto.builder()
                    .user(diary.getUser())
                    .commenterNickname(user.getNickname())
                    .diaryId(diaryId)
                    .build();
            notificationService.sendNotification(notification);
        }


        return DiaryCommentResponse.from(diaryComment);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("사용자를 찾을 수 없습니다."));
    }

    private Diary getDiary(Long diaryId) {
        return diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryException("여행일지 데이터를 찾을 수 없습니다."));
    }

}

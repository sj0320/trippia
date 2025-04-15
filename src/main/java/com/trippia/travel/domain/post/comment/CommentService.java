package com.trippia.travel.domain.post.comment;

import com.trippia.travel.domain.post.diary.Diary;
import com.trippia.travel.domain.post.diary.DiaryRepository;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.exception.diary.DiaryException;
import com.trippia.travel.exception.user.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.trippia.travel.domain.post.comment.CommentDto.*;
import static com.trippia.travel.domain.post.comment.CommentDto.CommentSaveRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    public CommentResponse saveDiaryComment(String email, Long diaryId, CommentSaveRequest request) {
        Diary diary = getDiary(diaryId);
        User user = getUserByEmail(email);
        String content = request.getContent();
        Comment comment = Comment.createComment(user, diary, content);

        diary.addComment(comment);
        commentRepository.save(comment);
        return CommentResponse.from(comment);
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

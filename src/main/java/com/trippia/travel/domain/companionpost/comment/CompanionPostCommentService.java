package com.trippia.travel.domain.companionpost.comment;

import com.trippia.travel.controller.dto.postcomment.request.CompanionPostCommentSaveRequest;
import com.trippia.travel.controller.dto.postcomment.response.CompanionPostCommentResponse;
import com.trippia.travel.domain.companionpost.post.CompanionPost;
import com.trippia.travel.domain.companionpost.post.CompanionPostRepository;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.exception.companionpost.CompanionPostException;
import com.trippia.travel.exception.user.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanionPostCommentService {

    private final UserRepository userRepository;
    private final CompanionPostRepository companionPostRepository;
    private final CompanionPostCommentRepository companionPostCommentRepository;

    @Transactional
    public CompanionPostCommentResponse savePostComment(String email, Long postId, CompanionPostCommentSaveRequest request) {
        User user = getUser(email);
        CompanionPost post = getPost(postId);
        String content = request.getContent();
        CompanionPostComment comment = CompanionPostComment.createComment(user, post, content);

        post.addComment(comment);
        companionPostCommentRepository.save(comment);
        return CompanionPostCommentResponse.from(comment);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("사용자를 찾을 수 없습니다."));
    }

    private CompanionPost getPost(Long postId) {
        return companionPostRepository.findById(postId)
                .orElseThrow(() -> new CompanionPostException("게시물을 찾을 수 없습니다."));
    }
}

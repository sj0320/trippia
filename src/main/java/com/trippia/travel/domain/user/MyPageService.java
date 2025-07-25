package com.trippia.travel.domain.user;

import com.trippia.travel.controller.dto.diary.response.DiarySummaryResponse;
import com.trippia.travel.controller.dto.diary.response.LikedDiarySummaryResponse;
import com.trippia.travel.controller.dto.plan.response.PlanSummaryResponse;
import com.trippia.travel.controller.dto.post.response.CompanionPostSummaryResponse;
import com.trippia.travel.controller.dto.user.response.MyPageResponse;
import com.trippia.travel.controller.dto.user.response.MyPageUserInfoResponse;
import com.trippia.travel.domain.companionpost.post.CompanionPostService;
import com.trippia.travel.domain.diarypost.diary.DiaryService;
import com.trippia.travel.domain.diarypost.likes.LikeService;
import com.trippia.travel.domain.travel.plan.PlanService;
import com.trippia.travel.exception.user.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final UserService userService;
    private final DiaryService diaryService;
    private final PlanService planService;
    private final LikeService likeService;
    private final CompanionPostService companionPostService;
    private final UserRepository userRepository;

    public MyPageResponse getMyPageInfo(Long userId) {
        // 사용자 정보 (프로필 이미지, 닉네임 ...)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("사용자를 찾을 수 없습니다."));
        String email = user.getEmail();

        MyPageUserInfoResponse myPageUserInfo = userService.getMyPageUserInfo(email);

        // 사용자가 작성한 여행일지
        List<DiarySummaryResponse> myDiaries = diaryService.getDiarySummariesByUser(email);

        // 사용자가 작성한 여행계획 (도시 사진, 제목, 시작날짜 ~ 종료날짜)
        List<PlanSummaryResponse> upcomingPlans = planService.getUpcomingPlanByUser(email);

        // 이전에 계획했던 이미 지난 여행계획 (도시 사진, 제목, 시작날짜 ~ 종료날짜)
        List<PlanSummaryResponse> pastPlans = planService.getPastPlanByUser(email);

        // 사용자가 좋아요 눌렀던 여행일지들 (썸네일, 제목, 좋아요, 조회수)
        List<LikedDiarySummaryResponse> likedDiaries = likeService.getLikedDiariesByUser(email);

        // 사용자가 작성한 여행 모집 게시글 (썸네일, 제목, 조회수)
        List<CompanionPostSummaryResponse> posts = companionPostService.getPostSummaryByUser(email);


        return MyPageResponse.builder()
                .myPageUserInfo(myPageUserInfo)
                .myDiaries(myDiaries)
                .upcomingPlans(upcomingPlans)
                .pastPlans(pastPlans)
                .likedDiaries(likedDiaries)
                .posts(posts)
                .build();

    }

}

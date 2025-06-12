package com.trippia.travel.controller.dto.user.response;

import com.trippia.travel.controller.dto.diary.response.DiarySummaryResponse;
import com.trippia.travel.controller.dto.diary.response.LikedDiarySummaryResponse;
import com.trippia.travel.controller.dto.plan.response.PlanSummaryResponse;
import com.trippia.travel.controller.dto.post.response.CompanionPostSummaryResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MyPageResponse {

    // 사용자 정보 (프로필 이미지, 닉네임 ...)
    private MyPageUserInfoResponse myPageUserInfo;

    // 사용자가 작성한 여행일지 (썸네일, 제목, 좋아요, 조회수)
    private List<DiarySummaryResponse> myDiaries;

    // 사용자가 작성한 여행계획 (도시 사진, 제목, 시작날짜 ~ 종료날짜)
    private List<PlanSummaryResponse> upcomingPlans;

    // 이전에 계획했던 이미 지난 여행계획 (도시 사진, 제목, 시작날짜 ~ 종료날짜)
    private List<PlanSummaryResponse> pastPlans;

    // 사용자가 좋아요 눌렀던 여행일지들 (썸네일, 제목, 좋아요, 조회수)
    private List<LikedDiarySummaryResponse> likedDiaries;

    private List<CompanionPostSummaryResponse> posts;

}

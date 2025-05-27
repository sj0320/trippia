package com.trippia.travel.domain.user;

import com.trippia.travel.controller.dto.diary.response.DiarySummaryResponse;
import com.trippia.travel.controller.dto.diary.response.LikedDiarySummaryResponse;
import com.trippia.travel.controller.dto.plan.response.PlanSummaryResponse;
import com.trippia.travel.controller.dto.user.response.MyPageResponse;
import com.trippia.travel.controller.dto.user.response.MyPageUserInfoResponse;
import com.trippia.travel.domain.post.diary.DiaryService;
import com.trippia.travel.domain.post.likes.LikeService;
import com.trippia.travel.domain.travel.plan.PlanService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@Transactional
@ExtendWith(MockitoExtension.class)
class MyPageServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private DiaryService diaryService;

    @Mock
    private PlanService planService;

    @Mock
    private LikeService likeService;

    @InjectMocks
    private MyPageService myPageService;

    @DisplayName("마이페이지 응답 데이터를 정상적으로 생성한다")
    @Test
    void getMyPageResponse() {
        // given
        String email = "test@example.com";

        MyPageUserInfoResponse userInfo = MyPageUserInfoResponse.builder()
                .userId(1L)
                .profileImageUrl("test")
                .nickname("test")
                .build();


        List<DiarySummaryResponse> myDiaries = List.of(
                DiarySummaryResponse.builder()
                        .diaryId(1L)
                        .title("test")
                        .thumbnailImageUrl("test")
                        .likeCount(10)
                        .viewCount(20)
                        .build()
        );

        List<PlanSummaryResponse> upcomingPlans = List.of(
                PlanSummaryResponse.builder()
                        .planId(1L)
                        .cityImage("test")
                        .title("test")
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusDays(1))
                        .build()
        );
        List<PlanSummaryResponse> pastPlans = List.of();
        List<LikedDiarySummaryResponse> likedDiaries = List.of();

        // mock 설정
        when(userService.getMyPageUserInfo(email)).thenReturn(userInfo);
        when(diaryService.getDiarySummariesByUser(email)).thenReturn(myDiaries);
        when(planService.getUpcomingPlanByUser(email)).thenReturn(upcomingPlans);
        when(planService.getPastPlanByUser(email)).thenReturn(pastPlans);
        when(likeService.getLikedDiariesByUser(email)).thenReturn(likedDiaries);

        // when
        MyPageResponse result = myPageService.getMyPageInfo(email);

        // then
        assertThat(result.getMyPageUserInfo()).isEqualTo(userInfo);
        assertThat(result.getMyDiaries()).hasSize(1);
        assertThat(result.getUpcomingPlans()).hasSize(1);
        assertThat(result.getPastPlans()).isEmpty();
        assertThat(result.getLikedDiaries()).isEmpty();

    }

}
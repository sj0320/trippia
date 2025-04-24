package com.trippia.travel.domain.post.diary;

import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.common.TravelCompanion;
import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.post.comment.Comment;
import com.trippia.travel.domain.user.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static com.trippia.travel.domain.post.diary.DiaryDto.SaveRequest;
import static com.trippia.travel.domain.post.diary.DiaryDto.UpdateDiaryDto;
import static org.assertj.core.api.Assertions.assertThat;

class DiaryTest {

    private static City stubCity;

    @BeforeAll
    static void setUp() {
        stubCity = new City() {
            @Override public Long getId() { return 1L; }
            @Override public String getName() { return "서울"; }
        };
    }

    @DisplayName("여행일지를 생성한다.")
    @Test
    void createDiary() {
        // given
        User user = createUser("test@example.com", "test", Role.ROLE_USER);
        SaveRequest request= createDiarySaveRequest("서울 여행", "가족");
        // when
        Diary diary = Diary.createDiary(request, user, stubCity, "testThumbnail");
        // then
        assertThat(diary.getUser()).isEqualTo(user);
        assertThat(diary.getCity().getName()).isEqualTo(request.getCityName());
        assertThat(diary.getCompanion().name()).isEqualTo("FAMILY");
    }

    @DisplayName("여행일지를 수정한다.")
    @Test
    void updateDiary() {
        // given
        User user = createUser("test@example.com", "test", Role.ROLE_USER);
        SaveRequest request= createDiarySaveRequest("서울 여행", "가족");
        Diary diary = Diary.createDiary(request, user, stubCity, "testThumbnail");
        UpdateDiaryDto updateDiaryDto = UpdateDiaryDto.builder()
                .title("부산 여행")
                .companion(TravelCompanion.FRIEND)
                .build();
        // when
        diary.update(updateDiaryDto);
        // then
        assertThat(diary.getTitle()).isEqualTo("부산 여행");
        assertThat(diary.getCompanion()).isEqualTo(TravelCompanion.FRIEND);
    }

    @Test
    @DisplayName("좋아요 추가/취소 테스트")
    void addAndCancelLike() {
        Diary diary = Diary.builder().likeCount(0).build();

        diary.addLike();
        diary.addLike();

        assertThat(diary.getLikeCount()).isEqualTo(2);

        diary.cancelLike();
        assertThat(diary.getLikeCount()).isEqualTo(1);
    }

    @DisplayName("댓글 작성을 작성한다.")
    @Test
    void addComment() {
        // given
        Diary diary = Diary.builder().build();
        Comment comment = Comment.builder().content("test").build();
        // when
        diary.addComment(comment);
        // then
        assertThat(diary.getComments()).hasSize(1);
        assertThat(comment.getDiary()).isEqualTo(diary); // 양방향 매핑 확인
    }

    private User createUser(String email, String nickname, Role role) {
        return User.builder()
                .email(email)
                .nickname(nickname)
                .password("pwd")
                .loginType(LoginType.LOCAL)
                .role(role)
                .profileImageUrl("http://test.image.url")
                .build();
    }

    private SaveRequest createDiarySaveRequest(String title, String companion) {
        return SaveRequest.builder()
                .cityId(1L)
                .cityName("서울")
                .title("서울 여행기")
                .content("test")
                .startDate(LocalDate.of(2024,3,20))
                .endDate(LocalDate.of(2024,3,25))
                .companion("가족")
                .rating(5)
                .totalBudget(100000)
                .themeIds(List.of(1L))
                .themeNames("힐링")
                .build();
    }

}
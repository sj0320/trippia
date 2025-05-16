package com.trippia.travel.domain.post.diary;

import com.trippia.travel.controller.dto.diary.request.DiarySaveRequest;
import com.trippia.travel.controller.dto.diary.request.UpdateDiaryDto;
import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.common.TravelCompanion;
import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.location.city.CityRepository;
import com.trippia.travel.domain.location.country.Country;
import com.trippia.travel.domain.location.country.CountryRepository;
import com.trippia.travel.domain.post.comment.Comment;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static com.trippia.travel.domain.common.CityType.*;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class DiaryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CityRepository cityRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @DisplayName("여행일지를 생성한다.")
    @Test
    void createDiary() {
        // given
        City seoul = setupCountriesAndCities();
        User user = createUser();
        DiarySaveRequest request= createDiarySaveRequest(seoul.getId());
        // when
        Diary diary = request.toEntity(user, seoul, "thumbnail");

        // then
        assertThat(diary.getUser()).isEqualTo(user);
        assertThat(diary.getCity().getName()).isEqualTo(seoul.getName());
        assertThat(diary.getCompanion().name()).isEqualTo("FRIEND");
    }

    @DisplayName("여행일지를 수정한다.")
    @Test
    void updateDiary() {
        // given
        City seoul = setupCountriesAndCities();
        User user = createUser();
        DiarySaveRequest request= createDiarySaveRequest(seoul.getId());
        Diary diary = request.toEntity(user, seoul, "thumbnail");
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

    private User createUser() {
        User user = User.builder()
                .email("email")
                .password("password")
                .nickname("test")
                .loginType(LoginType.LOCAL)
                .role(Role.ROLE_USER)
                .build();
        return userRepository.save(user);
    }

    private DiarySaveRequest createDiarySaveRequest(Long cityId) {
        return DiarySaveRequest.builder()
                .cityId(cityId)
                .title("test")
                .content("test content")
                .startDate(LocalDate.of(2099, 1, 1))
                .endDate(LocalDate.of(2099, 1, 10))
                .companion("친구")
                .rating(5)
                .totalBudget(1000000)
                .themeIds(List.of(1L))
                .build();
    }

    private City setupCountriesAndCities() {
        Country korea = Country.builder().name("한국").build();
        Country japan = Country.builder().name("일본").build();
        Country china = Country.builder().name("중국").build();
        countryRepository.saveAll(List.of(korea, japan, china));

        City city1 = City.builder()
                .name("서울")
                .country(korea)
                .cityType(KOREA)
                .build();

        City city2 = City.builder()
                .name("부산")
                .country(japan)
                .cityType(KOREA)
                .build();

        City city3 = City.builder()
                .name("도쿄")
                .country(china)
                .cityType(JAPAN)
                .build();

        City city4 = City.builder()
                .name("오사카")
                .country(korea)
                .cityType(JAPAN)
                .build();

        City city5 = City.builder()
                .name("상하이")
                .country(japan)
                .cityType(CHINA)
                .build();

        City city6 = City.builder()
                .name("베이징")
                .country(china)
                .cityType(CHINA)
                .build();

        cityRepository.saveAll(List.of(city1, city2, city3, city4, city5, city6));
        return city1;
    }

}
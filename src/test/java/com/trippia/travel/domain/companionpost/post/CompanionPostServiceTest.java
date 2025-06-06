package com.trippia.travel.domain.companionpost.post;

import com.trippia.travel.controller.dto.post.request.CompanionPostSaveRequest;
import com.trippia.travel.controller.dto.post.request.CompanionPostUpdateRequest;
import com.trippia.travel.domain.common.CityType;
import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.location.city.CityRepository;
import com.trippia.travel.domain.location.country.Country;
import com.trippia.travel.domain.location.country.CountryRepository;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.exception.companionpost.CompanionPostException;
import com.trippia.travel.file.FileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.trippia.travel.domain.common.CityType.JAPAN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class CompanionPostServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanionPostService companionPostService;

    @Autowired
    private CompanionPostRepository companionPostRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CityRepository cityRepository;

    @Mock
    private FileService fileService;

    @DisplayName("여행 모집 게시물을 작성한다.")
    @Test
    void savePost() {
        // given
        User user = createUser("email");
        Country japan = createCountry("일본");
        City tokyo = createCity("도쿄", japan, JAPAN);


        CompanionPostSaveRequest request = createCompanionPostSaveRequest("title", tokyo.getId(),
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(10));
        MockMultipartFile thumbnail = getMockMultipartFile();
        // when
        Long postId = companionPostService.savePost(user.getEmail(), request, thumbnail);

        // then
        CompanionPost post = companionPostRepository.findById(postId).get();
        assertThat(post.getCity()).isEqualTo(tokyo);
        assertThat(post.getUser()).isEqualTo(user);
        assertThat(post.getTitle()).isEqualTo(request.getTitle());

    }

    @DisplayName("여행 시작 날짜가 오늘 이전이면 예외가 발생한다.")
    @Test
    void savePost_InvalidDate() {
        // given
        User user = createUser("email");
        Country japan = createCountry("일본");
        City tokyo = createCity("도쿄", japan, JAPAN);

        CompanionPostSaveRequest request = createCompanionPostSaveRequest("title", tokyo.getId(),
                LocalDate.now().minusDays(2), LocalDate.now().minusDays(1));
        MockMultipartFile thumbnail = getMockMultipartFile();
        // when & then
        assertThatThrownBy(() -> companionPostService.savePost(user.getEmail(), request, thumbnail))
                .isInstanceOf(CompanionPostException.class);
    }

    @DisplayName("여행 계획 게시물을 수정한다.")
    @Test
    void editPost() {
        // given
        User user = createUser("email");
        Country japan = createCountry("일본");
        City tokyo = createCity("도쿄", japan, JAPAN);

        CompanionPost post = createPost(user, "title", tokyo);
        CompanionPostUpdateRequest request = CompanionPostUpdateRequest.builder()
                .title("title2")
                .genderRestriction(Gender.MALE)
                .cityId(tokyo.getId())
                .startDate(LocalDate.now().plusDays(5))
                .build();

        // when
        companionPostService.editPost(user.getEmail(), post.getId(), request, null);

        // then
        CompanionPost updatedPost = companionPostRepository.findById(post.getId()).get();
        assertThat(updatedPost.getTitle()).isEqualTo(request.getTitle());
        assertThat(updatedPost.getContent()).isEqualTo(request.getContent());
        assertThat(updatedPost.getStartDate()).isEqualTo(request.getStartDate());
        assertThat(updatedPost.getThumbnailUrl()).isEqualTo(tokyo.getImageUrl());
    }

    @DisplayName("여행모집 게시글을 삭제한다.")
    @Test
    void deletePost() {
        // given
        User user = createUser("email");
        Country japan = createCountry("일본");
        City tokyo = createCity("도쿄", japan, JAPAN);
        CompanionPost post = createPost(user, "title", tokyo);

        // when
        companionPostService.deletePost(user.getEmail(), post.getId());

        // then
        List<CompanionPost> result = companionPostRepository.findAll();
        assertThat(result).hasSize(0);
    }

    private CompanionPost createPost(User user, String title, City city) {
        CompanionPost post = CompanionPost.builder()
                .user(user)
                .title(title)
                .city(city)
                .content("content_" + title)
                .genderRestriction(Gender.ALL)
                .startDate(LocalDate.now().plusDays(10))
                .endDate(LocalDate.now().plusDays(20))
                .build();
        return companionPostRepository.save(post);
    }


    private User createUser(String email) {
        User user = User.builder()
                .email(email)
                .password("password")
                .nickname("nick_ " + email)
                .loginType(LoginType.LOCAL)
                .role(Role.ROLE_USER)
                .build();
        return userRepository.save(user);
    }

    private Country createCountry(String name) {
        return countryRepository.save(Country.builder().name(name).build());
    }

    private City createCity(String name, Country country, CityType cityType) {
        return cityRepository.save(City.builder()
                .name(name)
                .country(country)
                .cityType(cityType)
                .build());
    }

    private CompanionPostSaveRequest createCompanionPostSaveRequest(String title, Long cityId, LocalDate startDate, LocalDate endDate) {
        return CompanionPostSaveRequest.builder()
                .cityId(cityId)
                .title(title)
                .content(title + "_content")
                .genderRestriction(Gender.ALL)
                .recruitmentCount(5)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    private MockMultipartFile getMockMultipartFile() {
        return new MockMultipartFile(
                "test thumbnail",
                "thumbnail.png",
                MediaType.IMAGE_PNG_VALUE,
                "thumbnail".getBytes()
        );
    }

}
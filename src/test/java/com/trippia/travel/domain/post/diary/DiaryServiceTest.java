package com.trippia.travel.domain.post.diary;

import com.trippia.travel.domain.common.TravelCompanion;
import com.trippia.travel.domain.post.diarytheme.DiaryThemeRepository;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.exception.diary.DiaryException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static com.trippia.travel.domain.post.diary.DiaryDto.SaveRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DiaryServiceTest {

    @Container
    static MariaDBContainer MARIADB_CONTAINER = new MariaDBContainer("mariadb:10.11");

    @Autowired
    DiaryService diaryService;

    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DiaryThemeRepository diaryThemeRepository;

    @AfterEach
    void tearDown() {
        diaryThemeRepository.deleteAllInBatch();
        diaryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @BeforeEach
    void cleanDatabase() {
        diaryThemeRepository.deleteAllInBatch();
        diaryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("여행일지를 생성하고 저장한다.")
    @Test
    void saveDiary() {
        // given
        User user = createUser();
        userRepository.save(user);

        SaveRequest request = createSaveRequest("test", "친구", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 10));
        String email = user.getEmail();

        // when
        diaryService.saveDiary(email, request);

        // then
        Diary savedDiary = diaryRepository.findAll().get(0);

        assertThat(savedDiary.getTitle()).isEqualTo(request.getTitle());
        assertThat(savedDiary.getCompanion()).isEqualTo(TravelCompanion.FRIEND);
        assertThat(savedDiary.getStartDate()).isEqualTo(request.getStartDate());
        assertThat(savedDiary.getEndDate()).isEqualTo(request.getEndDate());
    }


    @DisplayName("여행 시작 날짜는 여행 종료 날짜보다 이후일 수 없다.")
    @Test
    void saveDiaryWhenStartDiaryOverEndDate() {
        // given
        User user = createUser();
        userRepository.save(user);

        SaveRequest request = createSaveRequest("test", "연인",
                LocalDate.of(2024, 2, 10), // startDate
                LocalDate.of(2024, 2, 1)); // endDate (startDate보다 이전)

        String email = user.getEmail();

        // when & then
        assertThatThrownBy(() -> diaryService.saveDiary(email, request))
                .isInstanceOf(DiaryException.class);
    }

    @DisplayName("여행 시작 날짜가 오늘 이후라면 예외가 발생해야 한다.")
    @Test
    void saveDiaryWhenStartDateOverToday() {
        // given
        User user = createUser();
        userRepository.save(user);


        LocalDate pastDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(5);
        SaveRequest request = createSaveRequest("test", "연인",
                pastDate,
                endDate);
        String email = user.getEmail();
        // when & then
        assertThatThrownBy(()-> diaryService.saveDiary(email, request))
                .isInstanceOf(DiaryException.class);
    }

    private SaveRequest createSaveRequest(String title, String companion, LocalDate startDate, LocalDate endDate) {
        return SaveRequest.builder()
                .cityId(1L)
                .title(title)
                .content("hello trippia")
                .startDate(startDate)
                .endDate(endDate)
                .companion(companion)
                .rating(5)
                .totalBudget(1_000_000)
                .themeIds(List.of(1L, 2L))
                .build();
    }

    private static User createUser() {
        return User.builder()
                .email("test@example.com")
                .nickname("tester")
                .password("test")
                .build();
    }


}
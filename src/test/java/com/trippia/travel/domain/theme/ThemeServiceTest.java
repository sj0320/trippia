package com.trippia.travel.domain.theme;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.trippia.travel.domain.theme.ThemeDto.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class ThemeServiceTest {

    @Autowired
    private ThemeService themeService;

    @Autowired
    private ThemeRepository themeRepository;

    @DisplayName("모든 여행테마들을 가져온다.")
    @Test
    void getAllThemes() {
        // given
        Theme theme1 = Theme.builder().name("액티비티").build();
        Theme theme2 = Theme.builder().name("문화").build();
        Theme theme3 = Theme.builder().name("맛집 탐방").build();
        themeRepository.saveAll(List.of(theme1, theme2, theme3));
        // when
        List<ThemeResponse> themes = themeService.getAllThemes();

        // then
        Assertions.assertThat(themes).hasSize(3)
                .extracting("name")
                .containsExactlyInAnyOrder("액티비티", "문화", "맛집 탐방");
    }

}
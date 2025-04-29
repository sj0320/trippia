package com.trippia.travel.domain.location.city;

import com.trippia.travel.domain.location.country.Country;
import com.trippia.travel.domain.location.country.CountryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static com.trippia.travel.domain.common.CityType.*;
import static com.trippia.travel.domain.location.city.CityDto.CityGroupedByTypeResponse;
import static com.trippia.travel.domain.location.city.CityDto.CitySummary;

@ActiveProfiles("test")
@SpringBootTest
class CityServiceTest {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CityService cityService;

    @AfterEach
    void tearDown() {
        cityRepository.deleteAllInBatch();
    }

    @DisplayName("도시들을 나라별로 그룹화하여 조회할 수 있다.")
    @Test
    void getCitiesGroupedByType() {
        // given
        setupCountriesAndCities();
        // when
        CityGroupedByTypeResponse citiesGroupedByType = cityService.getCitiesGroupedByType();
        // then
        Map<String, List<CitySummary>> groupedCities = citiesGroupedByType.getGroupedCities();

        Assertions.assertThat(groupedCities.get("한국"))
                .extracting("name")
                .containsExactlyInAnyOrder("서울", "부산");

        Assertions.assertThat(groupedCities.get("일본"))
                .extracting("name")
                .containsExactlyInAnyOrder("도쿄", "오사카");

        Assertions.assertThat(groupedCities.get("중국"))
                .extracting("name")
                .containsExactlyInAnyOrder("상하이", "베이징");


    }

    private void setupCountriesAndCities() {
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
    }

}
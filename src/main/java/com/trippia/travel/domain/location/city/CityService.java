package com.trippia.travel.domain.location.city;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.trippia.travel.domain.location.city.CityDto.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CityService {

    private final CityRepository cityRepository;

    @Transactional
    public CityGroupedByTypeResponse getCitiesGroupedByType() {
        List<City> cities = cityRepository.findAll();

        Map<String, List<CitySummary>> groupedCities = groupCitiesByType(cities);
        return new CityGroupedByTypeResponse(groupedCities);
    }

    // CityType별로 도시를 그룹화하는 메서드
    private Map<String, List<CitySummary>> groupCitiesByType(List<City> cities) {
        return cities.stream()
                .collect(Collectors.groupingBy(
                        city -> city.getCityType().getName(),  // CityType에 따른 그룹화
                        Collectors.mapping(
                                city -> new CitySummary(city.getId(), city.getName()),  // City -> CitySummary 변환
                                Collectors.toList())  // 그룹화된 도시 리스트로 반환
                ));
    }

}

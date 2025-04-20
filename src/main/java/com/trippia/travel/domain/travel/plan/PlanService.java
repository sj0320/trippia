package com.trippia.travel.domain.travel.plan;

import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.location.city.CityRepository;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.exception.user.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.trippia.travel.domain.travel.plan.PlanDto.PlanCreateRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanService {

    private final PlanRepository planRepository;
    private final CityRepository cityRepository;
    private final UserRepository userRepository;

    public void createPlan(String email, PlanCreateRequest request) {
        User user = getUserByEmail(email);

        LocalDate startDate = LocalDate.parse(request.getStartDate());
        LocalDate endDate = LocalDate.parse(request.getEndDate());
        List<Long> requestCityIds = request.getCityIds();

        List<City> cityIds = cityRepository.findAllById(requestCityIds);

        String title = getDefaultTitleByCities(cityIds);
        log.info("일정 제목={}", title);

        Plan plan = Plan.createPlan(user, title, startDate, endDate);
        planRepository.save(plan);
    }

    private String getDefaultTitleByCities(List<City> cityIds) {
        return cityIds.stream()
                .map(City::getName)
                .collect(Collectors.joining(", ")) + " 여행";
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("사용자를 찾을 수 없습니다."));
    }


}

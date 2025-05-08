package com.trippia.travel.domain.travel.plan;

import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.location.city.CityRepository;
import com.trippia.travel.domain.travel.plancity.PlanCity;
import com.trippia.travel.domain.travel.plancity.PlanCityRepository;
import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.domain.travel.schedule.ScheduleRepository;
import com.trippia.travel.domain.travel.scheduleitem.ScheduleItem;
import com.trippia.travel.domain.travel.scheduleitem.ScheduleItemRepository;
import com.trippia.travel.domain.travel.scheduleitem.memo.Memo;
import com.trippia.travel.domain.travel.scheduleitem.scheduleplace.SchedulePlace;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.exception.plan.PlanException;
import com.trippia.travel.exception.user.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.trippia.travel.controller.dto.PlanDto.PlanCreateRequest;
import static com.trippia.travel.controller.dto.PlanDto.PlanDetailsResponse;
import static com.trippia.travel.controller.dto.ScheduleDto.ScheduleDetailsResponse;
import static com.trippia.travel.controller.dto.ScheduleItemDto.ScheduleItemResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PlanService {

    private final PlanRepository planRepository;
    private final CityRepository cityRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final PlanCityRepository planCityRepository;
    private final ScheduleItemRepository scheduleItemRepository;

    @Transactional
    public Long createPlan(String email, PlanCreateRequest request) {
        User user = getUserByEmail(email);

        LocalDate startDate = LocalDate.parse(request.getStartDate());
        LocalDate endDate = LocalDate.parse(request.getEndDate());
        List<Long> requestCityIds = request.getCityIds();

        log.info("requestCityId={}", requestCityIds.get(0));
        List<City> cities = cityRepository.findAllById(requestCityIds);


        String title = getDefaultTitleByCities(cities);
        log.info("일정 제목={}", title);

        Plan plan = Plan.createPlan(user, title, startDate, endDate);

        for (City city : cities) {
            PlanCity planCity = PlanCity.builder().city(city).plan(plan).build();
            plan.addPlanCity(planCity);
        }

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Schedule schedule = new Schedule(plan, date);
            plan.addSchedule(schedule);
        }
        Plan savedPlan = planRepository.save(plan);
        return savedPlan.getId();
    }

    public PlanDetailsResponse findPlan(String email, Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new PlanException("여행 계획을 찾을 수 없습니다."));
        plan.validateOwnerOf(email);

        List<PlanCity> planCities = plan.getPlanCities();

        // schedule(scheduleItems... ) 불러오기
        List<Schedule> schedules = plan.getSchedules();
        List<Long> scheduleIds = schedules.stream()
                .map(Schedule::getId)
                .toList();
        List<ScheduleItem> scheduleItems = scheduleItemRepository.findAllByScheduleIdIn(scheduleIds);

        Map<Long, List<ScheduleItem>> scheduleItemMap = scheduleItems.stream()
                .collect(Collectors.groupingBy(item -> item.getSchedule().getId()));

        List<ScheduleDetailsResponse> scheduleDetailsResponse = schedules.stream()
                .map(schedule -> {
                    List<ScheduleItemResponse> itemResponses = scheduleItemMap
                            .getOrDefault(schedule.getId(), List.of())
                            .stream()
                            .map(item -> {
                                if (item instanceof Memo memo) {
                                    return ScheduleItemResponse.ofMemo(memo);
                                } else if (item instanceof SchedulePlace schedulePlace) {
                                    return ScheduleItemResponse.ofPlace(schedulePlace);
                                } else {
                                    throw new IllegalArgumentException("Unknow ItemType");
                                }
                            })
                            .toList();
                    return new ScheduleDetailsResponse(schedule.getId(), schedule.getDate(), itemResponses);
                })
                .toList();
        return PlanDetailsResponse.of(plan, planCities, scheduleDetailsResponse);
    }

    private String getDefaultTitleByCities(List<City> cities) {
        return cities.stream()
                .map(City::getName)
                .collect(Collectors.joining(", ")) + " 여행";
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("사용자를 찾을 수 없습니다."));
    }

}

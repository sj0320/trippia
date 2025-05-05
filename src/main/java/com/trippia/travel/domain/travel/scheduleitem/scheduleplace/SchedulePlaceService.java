package com.trippia.travel.domain.travel.scheduleitem.scheduleplace;

import com.trippia.travel.domain.location.place.Place;
import com.trippia.travel.domain.location.place.PlaceRepository;
import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.domain.travel.schedule.ScheduleRepository;
import com.trippia.travel.domain.travel.scheduleitem.ScheduleItem;
import com.trippia.travel.domain.travel.scheduleitem.ScheduleItemRepository;
import com.trippia.travel.exception.schedule.ScheduleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.trippia.travel.controller.dto.SchedulePlaceDto.PlaceSaveRequest;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SchedulePlaceService {

    private final SchedulePlaceRepository schedulePlaceRepository;
    private final PlaceRepository placeRepository;
    private final ScheduleItemRepository scheduleItemRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public void savePlace(String email, PlaceSaveRequest request) {
        Schedule schedule = getSchedule(request.getScheduleId());
        schedule.validateOwnerOf(email);

        // Place 저장
        Place place = placeRepository.save(request.toEntity());

        // scheduleItem 저장
        ScheduleItem scheduleItem = scheduleItemRepository.save(ScheduleItem.ofPlace(schedule, place));

        // SchedulePlace 저장
        SchedulePlace schedulePlace = SchedulePlace.builder().place(place).scheduleItem(scheduleItem).build();
        schedulePlaceRepository.save(schedulePlace);
    }

    private Schedule getSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException("해당 스케줄을 찾을 수 없습니다."));
    }

}

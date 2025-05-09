package com.trippia.travel.domain.travel.scheduleitem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleItemRepository extends JpaRepository<ScheduleItem,Long> {

    List<ScheduleItem> findAllByScheduleIdIn(List<Long> scheduleIds);

    List<ScheduleItem> findByScheduleId(Long scheduleId);
}

package com.trippia.travel.domain.travel.scheduleitem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleItemRepository extends JpaRepository<ScheduleItem, Long> {


    @Query("SELECT s FROM ScheduleItem s WHERE s.schedule.id IN :scheduleIds ORDER BY s.schedule.id, s.sequence ASC")
    List<ScheduleItem> findAllByScheduleIdInOrderBySequence(@Param("scheduleIds") List<Long> scheduleIds);

    List<ScheduleItem> findByScheduleId(Long scheduleId);

    @Query("SELECT MAX(s.sequence) FROM ScheduleItem s WHERE s.schedule.id = :scheduleId")
    Optional<Integer> findLastSequenceByScheduleId(@Param("scheduleId") Long scheduleId);

    List<ScheduleItem> findByScheduleIdOrderBySequence(Long scheduleId);
}

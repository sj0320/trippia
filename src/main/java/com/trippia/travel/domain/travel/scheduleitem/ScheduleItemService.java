package com.trippia.travel.domain.travel.scheduleitem;

import com.trippia.travel.controller.dto.scheduleitem.requset.ScheduleItemOrderRequest;
import com.trippia.travel.controller.dto.scheduleitem.response.ScheduleItemResponse;
import com.trippia.travel.domain.travel.planparticipant.PlanParticipantRepository;
import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.domain.travel.schedule.ScheduleRepository;
import com.trippia.travel.domain.travel.scheduleitem.memo.Memo;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.exception.schedule.ScheduleException;
import com.trippia.travel.exception.scheduleitem.ScheduleItemException;
import com.trippia.travel.exception.user.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

import static com.trippia.travel.controller.dto.scheduleitem.requset.ScheduleItemOrderRequest.ScheduleItemOrder;
import static com.trippia.travel.domain.travel.planparticipant.InvitationStatus.ACCEPTED;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleItemService {

    private final UserRepository userRepository;
    private final ScheduleItemRepository scheduleItemRepository;
    private final ScheduleRepository scheduleRepository;
    private final PlanParticipantRepository planParticipantRepository;


    @Transactional
    public void deleteScheduleItem(String email, Long scheduleItemId) {
        ScheduleItem scheduleItem = getScheduleItem(scheduleItemId);
        Schedule schedule = scheduleItem.getSchedule();
        validatePermission(getUser(email), schedule);

        scheduleItemRepository.deleteById(scheduleItemId);
    }

    public List<ScheduleItemResponse> getScheduleItemsByScheduleId(String email, Long scheduleId) {
        Schedule schedule = getSchedule(scheduleId);
        validatePermission(getUser(email), schedule);
        validatePermission(getUser(email), schedule);

        List<ScheduleItem> scheduleItems = scheduleItemRepository.findByScheduleIdOrderBySequence(scheduleId);
        return ScheduleItemConverter.toResponses(scheduleItems);
    }

    @Transactional
    public void updateScheduleItemMeta(String email, Long scheduleItemId, Integer expectedCost, LocalTime executionTime) {
        ScheduleItem scheduleItem = getScheduleItem(scheduleItemId);
        Schedule schedule = scheduleItem.getSchedule();
        validatePermission(getUser(email), schedule);
        scheduleItem.updateMeta(expectedCost, executionTime);
    }

    @Transactional
    public void updateMemo(String email, Long scheduleItemId, String content) {
        ScheduleItem scheduleItem = getScheduleItem(scheduleItemId);
        if (!(scheduleItem instanceof Memo memo)) {
            throw new ScheduleItemException("유효하지 않은 요청입니다");
        }
        Schedule schedule = scheduleItem.getSchedule();
        validatePermission(getUser(email), schedule);
        memo.updateContent(content);
    }

    @Transactional
    public void reorderItems(String email, ScheduleItemOrderRequest request) {
        Schedule schedule = getSchedule(request.getScheduleId());
        validatePermission(getUser(email), schedule);

        List<ScheduleItemOrder> orders = request.getOrders();
        for(ScheduleItemOrder order : orders){
            ScheduleItem scheduleItem = getScheduleItem(order.getItemId());
            if(schedule!=scheduleItem.getSchedule()){
                throw new ScheduleItemException("아이템이 해당 스케줄에 속하지 않습니다.");
            }
            scheduleItem.updateSequence(order.getSequence());
        }
    }

    private Schedule getSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException("일정을 찾을 수 없습니다."));
    }

    private ScheduleItem getScheduleItem(Long scheduleItemId) {
        return scheduleItemRepository.findById(scheduleItemId)
                .orElseThrow(() -> new ScheduleItemException("스케줄 항목을 찾을 수 없습니다."));
    }

    private void validatePermission(User user, Schedule schedule) {
        Long planId = schedule.getPlan().getId();
        boolean hasPermission = planParticipantRepository.existsByUserIdAndPlanIdAndStatus(user.getId(), planId, ACCEPTED);
        if (!hasPermission) {
            throw new ScheduleException("접근 권한이 없습니다.");
        }
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("사용자를 찾을 수 없습니다."));
    }

}

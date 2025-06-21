package com.trippia.travel.domain.travel.plan;

import com.trippia.travel.controller.dto.plan.request.PlanCreateRequest;
import com.trippia.travel.controller.dto.plan.request.PlanUpdateRequest;
import com.trippia.travel.controller.dto.plan.response.PlanDetailsResponse;
import com.trippia.travel.controller.dto.plan.response.PlanSummaryResponse;
import com.trippia.travel.controller.dto.planparticipant.response.PlanParticipantResponse;
import com.trippia.travel.controller.dto.planparticipant.response.ReceivedPlanParticipantResponse;
import com.trippia.travel.controller.dto.planparticipant.response.RequestPlanParticipantResponse;
import com.trippia.travel.controller.dto.schedule.response.ScheduleDetailsResponse;
import com.trippia.travel.controller.dto.scheduleitem.response.ScheduleItemResponse;
import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.location.city.CityRepository;
import com.trippia.travel.domain.notification.NotificationService;
import com.trippia.travel.domain.notification.dto.ParticipantAcceptedNotificationDto;
import com.trippia.travel.domain.notification.dto.ParticipantInvitedNotificationDto;
import com.trippia.travel.domain.travel.plancity.PlanCity;
import com.trippia.travel.domain.travel.planparticipant.PlanParticipant;
import com.trippia.travel.domain.travel.planparticipant.PlanParticipantRepository;
import com.trippia.travel.domain.travel.schedule.Schedule;
import com.trippia.travel.domain.travel.scheduleitem.ScheduleItem;
import com.trippia.travel.domain.travel.scheduleitem.ScheduleItemConverter;
import com.trippia.travel.domain.travel.scheduleitem.ScheduleItemRepository;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.exception.BaseException;
import com.trippia.travel.exception.plan.PlanException;
import com.trippia.travel.exception.user.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.trippia.travel.domain.travel.planparticipant.InvitationStatus.ACCEPTED;
import static com.trippia.travel.domain.travel.planparticipant.InvitationStatus.PENDING;
import static com.trippia.travel.domain.travel.planparticipant.PlanRole.OWNER;
import static com.trippia.travel.domain.travel.planparticipant.PlanRole.PARTICIPANT;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PlanService {

    private final PlanRepository planRepository;
    private final PlanParticipantRepository planParticipantRepository;
    private final CityRepository cityRepository;
    private final UserRepository userRepository;
    private final ScheduleItemRepository scheduleItemRepository;
    private final NotificationService notificationService;

    @Transactional
    public Long createPlan(String email, PlanCreateRequest request) {
        User user = getUser(email);

        LocalDate startDate = LocalDate.parse(request.getStartDate());
        LocalDate endDate = LocalDate.parse(request.getEndDate());
        List<Long> requestCityIds = request.getCityIds();

        List<City> cities = cityRepository.findAllById(requestCityIds);

        String title = getDefaultTitleByCities(cities);

        Plan plan = Plan.createPlan(user.getEmail(), title, startDate, endDate);

        for (City city : cities) {
            PlanCity planCity = PlanCity.builder().city(city).plan(plan).build();
            plan.addPlanCity(planCity);
        }

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Schedule schedule = new Schedule(plan, date);
            plan.addSchedule(schedule);
        }
        Plan savedPlan = planRepository.save(plan);
        PlanParticipant participant = PlanParticipant.builder()
                .user(user)
                .plan(savedPlan)
                .role(OWNER)
                .status(ACCEPTED)
                .build();
        planParticipantRepository.save(participant);

        return savedPlan.getId();
    }

    public PlanDetailsResponse findPlan(String email, Long planId) {
        Plan plan = getPlan(planId);
        validatePlanPermission(getUser(email), planId);
        List<PlanCity> planCities = plan.getPlanCities();

        // schedule(scheduleItems... ) 불러오기
        List<Schedule> schedules = plan.getSchedules();
        List<Long> scheduleIds = schedules.stream()
                .map(Schedule::getId)
                .toList();
        List<ScheduleItem> scheduleItems = scheduleItemRepository.findAllByScheduleIdInOrderBySequence(scheduleIds);


        Map<Long, List<ScheduleItem>> scheduleItemMap = scheduleItems.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getSchedule().getId(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<ScheduleDetailsResponse> scheduleDetailsResponse = schedules.stream()
                .map(schedule -> {
                    List<ScheduleItem> items = scheduleItemMap.getOrDefault(schedule.getId(), List.of());
                    List<ScheduleItemResponse> itemResponses = ScheduleItemConverter.toResponses(items);
                    return ScheduleDetailsResponse.builder()
                            .id(schedule.getId())
                            .date(schedule.getDate())
                            .scheduleItems(itemResponses)
                            .build();
                })
                .toList();

        List<PlanParticipant> participants = planParticipantRepository.findByPlanIdAndStatus(planId, ACCEPTED);
        List<PlanParticipantResponse> participantResponse = participants.stream()
                .map(participant -> PlanParticipantResponse.builder()
                        .userId(participant.getUser().getId())
                        .nickname(participant.getUser().getNickname())
                        .profileImageUrl(participant.getUser().getProfileImageUrl())
                        .role(participant.getRole().name())
                        .build())
                .toList();
        return PlanDetailsResponse.of(plan, planCities, scheduleDetailsResponse, participantResponse);
    }

    public List<PlanSummaryResponse> getUpcomingPlanByUser(String email) {
        User user = getUser(email);
        List<Plan> plans = planRepository.findUpcomingPlansByUser(user.getId(), LocalDate.now());
        return mapPlansToSummaryResponses(plans);
    }

    public List<PlanSummaryResponse> getPastPlanByUser(String email) {
        User user = getUser(email);
        List<Plan> plans = planRepository.findPastPlansByUser(user.getId(), LocalDate.now());
        return mapPlansToSummaryResponses(plans);
    }

    @Transactional
    public void deletePlan(String email, Long planId) {
        User user = getUser(email);
        validatePlanPermission(user, planId);

        planParticipantRepository.deleteByPlanId(planId);
        Plan plan = getPlan(planId);
        planRepository.delete(plan);
    }

    @Transactional
    public void invitePlan(String email, Long planId, String nickname) {
        Plan plan = getPlan(planId);
        plan.validateOwnerOf(email);
        User inviterUser = getUser(email);
        User invitedUser = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UserException("사용자를 찾을 수 없습니다."));

        validateInviteCondition(invitedUser.getId(), planId);

        // 저장
        PlanParticipant participant = PlanParticipant.builder()
                .user(invitedUser)
                .plan(plan)
                .role(PARTICIPANT)
                .status(PENDING)
                .build();

        planParticipantRepository.save(participant);

        ParticipantInvitedNotificationDto notification = ParticipantInvitedNotificationDto.builder()
                .user(invitedUser)
                .inviterNickname(inviterUser.getNickname())
                .planId(planId)
                .build();
        notificationService.sendNotification(notification);
    }

    @Transactional
    public void acceptPlanInvite(String email, Long planId) {
        User user = getUser(email);
        PlanParticipant participant = planParticipantRepository.findByUserIdAndPlanId(user.getId(), planId)
                .orElseThrow(() -> new IllegalArgumentException("초대 내역이 존재하지 않습니다."));
        if (!participant.getStatus().equals(PENDING)) {
            throw new IllegalArgumentException("이미 처리된 초대입니다.");
        }
        notifyOtherParticipantsOfAcceptance(user, planId);
        participant.acceptInvitation();

    }

    @Transactional
    public void updatePlan(String email, Long planId, PlanUpdateRequest request) {
        Plan plan = getPlan(planId);
        plan.validateOwnerOf(email);
        plan.updatePlan(request.getTitle(), request.getStartDate(), request.getEndDate());
    }

    public List<ReceivedPlanParticipantResponse> getReceivedParticipantRequests(String email) {
        User user = getUser(email);
        return planParticipantRepository.findReceivedRequests(user.getId());
    }

    public List<RequestPlanParticipantResponse> getSentParticipantRequests(String email) {
        User user = getUser(email);
        return planParticipantRepository.findSentRequests(user.getId());
    }

    @Transactional
    public void rejectPlanInvite(String email, Long planId) {
        User user = getUser(email);
        planParticipantRepository.deleteByUserIdAndPlanId(user.getId(), planId);
    }

    @Transactional
    public void cancelPlanInvite(String email, Long planId, String nickname) {
        Plan plan = getPlan(planId);
        plan.validateOwnerOf(email);

        User targetUser = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UserException("존재하지 않는 사용자 입니다."));
        planParticipantRepository.deleteByUserIdAndPlanId(targetUser.getId(), planId);
    }

    private Plan getPlan(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new BaseException("여행 계획을 찾을 수 없습니다."));
    }

    private String getDefaultTitleByCities(List<City> cities) {
        return cities.stream()
                .map(City::getName)
                .collect(Collectors.joining(", ")) + " 여행";
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("사용자를 찾을 수 없습니다."));
    }

    private List<PlanSummaryResponse> mapPlansToSummaryResponses(List<Plan> plans) {
        return plans.stream()
                .map(plan -> {
                    String imageUrl = getRandomCityImageUrl(plan.getPlanCities());
                    return PlanSummaryResponse.from(plan, imageUrl);
                })
                .toList();
    }

    private String getRandomCityImageUrl(List<PlanCity> planCities) {
        if (planCities == null || planCities.isEmpty()) return "";
        PlanCity randomPlanCity = planCities.get(ThreadLocalRandom.current().nextInt(planCities.size()));
        return randomPlanCity.getCity() != null ? randomPlanCity.getCity().getImageUrl() : "";
    }

    private void validatePlanPermission(User user, Long planId) {
        boolean hasPermission = planParticipantRepository.existsByUserIdAndPlanIdAndStatus(user.getId(), planId, ACCEPTED);
        if (!hasPermission) {
            throw new BaseException("접근 권한이 없습니다.");
        }
    }

    private void validateInviteCondition(Long userId, Long planId) {
        boolean isAlreadyJoin = planParticipantRepository.existsByUserIdAndPlanIdAndStatus(userId, planId, ACCEPTED);
        boolean isAlreadyInvite = planParticipantRepository.existsByUserIdAndPlanIdAndStatus(userId, planId, PENDING);
        if (isAlreadyJoin || isAlreadyInvite) {
            throw new PlanException("이미 초대되었거나 참여중인 사용자입니다.");
        }
    }

    private void notifyOtherParticipantsOfAcceptance(User user, Long planId) {
        List<PlanParticipant> recipients = planParticipantRepository.findByPlanIdAndStatus(planId, ACCEPTED)
                .stream()
                .filter(p -> !p.getUser().getId().equals(user.getId()))
                .toList();

        for (PlanParticipant other : recipients) {
            ParticipantAcceptedNotificationDto notification = ParticipantAcceptedNotificationDto.builder()
                    .user(other.getUser())
                    .accepterNickname(user.getNickname())
                    .planId(planId)
                    .build();

            notificationService.sendNotification(notification);
        }
    }

}

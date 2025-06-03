package com.trippia.travel.domain.travel.planparticipant;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.trippia.travel.controller.dto.planparticipant.response.ReceivedPlanParticipantResponse;
import com.trippia.travel.controller.dto.planparticipant.response.RequestPlanParticipantResponse;
import com.trippia.travel.domain.travel.plan.QPlan;
import com.trippia.travel.domain.user.QUser;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PlanParticipantRepositoryCustomImpl implements PlanParticipantRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    QPlanParticipant pp = QPlanParticipant.planParticipant;
    QPlan p = QPlan.plan;
    QUser u = QUser.user; // 요청을 받은 유저 (현재 로그인 유저)
    QUser owner = new QUser("owner"); // Plan.ownerEmail 과 조인될 유저 (요청 보낸 사람)
    QUser participant = new QUser("participant"); // 초대받은 사람 (보낸 요청 조회용)

    // 받은 요청 조회
    @Override
    public List<ReceivedPlanParticipantResponse> findReceivedRequests(Long userId) {
        return queryFactory
                .select(Projections.constructor(
                        ReceivedPlanParticipantResponse.class,
                        p.id.as("planId"),
                        p.title.as("planTitle"),
                        owner.nickname,
                        owner.profileImageUrl
                ))
                .from(pp)
                .join(pp.plan, p)
                .join(u).on(pp.user.eq(u)) // 받은 사람
                .join(owner).on(p.ownerEmail.eq(owner.email)) // 보낸 사람
                .where(
                        u.id.eq(userId),
                        pp.status.eq(InvitationStatus.PENDING)
                )
                .fetch();
    }

    // 보낸 요청 조회
    @Override
    public List<RequestPlanParticipantResponse> findSentRequests(Long userId) {
        return queryFactory
                .select(Projections.constructor(
                        RequestPlanParticipantResponse.class,
                        p.id.as("planId"),
                        p.title.as("planTitle"),
                        participant.nickname,
                        participant.profileImageUrl
                ))
                .from(pp)
                .join(pp.plan, p)
                .join(participant).on(pp.user.eq(participant)) // 초대받은 사용자
                .join(owner).on(p.ownerEmail.eq(owner.email))  // 보낸 사람
                .where(
                        owner.id.eq(userId), // 요청을 보낸 사람이 userId
                        pp.status.eq(InvitationStatus.PENDING)
                )
                .fetch();
    }
}

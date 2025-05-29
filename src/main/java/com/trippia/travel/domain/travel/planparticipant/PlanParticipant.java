package com.trippia.travel.domain.travel.planparticipant;

import com.trippia.travel.domain.travel.plan.Plan;
import com.trippia.travel.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PlanParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    private PlanRole role;

    @Builder
    private PlanParticipant(User user, Plan plan, PlanRole role) {
        this.user = user;
        this.plan = plan;
        this.role = role;
    }
}

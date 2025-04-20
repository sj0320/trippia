package com.trippia.travel.domain.travel.plan;

import com.trippia.travel.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Plan {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="plan_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    private LocalDate startDate;

    private LocalDate endDate;

    public static Plan createPlan(User user, String title, LocalDate startDate, LocalDate endDate){
        return Plan.builder()
                .user(user)
                .title(title)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }


}

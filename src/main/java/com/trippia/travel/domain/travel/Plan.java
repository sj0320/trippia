package com.trippia.travel.domain.travel;

import com.trippia.travel.domain.user.User;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Plan {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="plan_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String name;

    private LocalDate startDate;

    private LocalDate endDate;

}

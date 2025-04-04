package com.trippia.travel.domain.theme;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Theme {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="theme_id")
    private Long id;

    private String name;

}

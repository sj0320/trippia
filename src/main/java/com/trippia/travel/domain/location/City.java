package com.trippia.travel.domain.location;

import com.trippia.travel.domain.common.CityType;
import jakarta.persistence.*;

@Entity
public class City {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="city_id")
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @Enumerated(EnumType.STRING)
    private CityType cityType;

}

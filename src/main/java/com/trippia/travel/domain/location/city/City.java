package com.trippia.travel.domain.location.city;

import com.trippia.travel.domain.common.CityType;
import com.trippia.travel.domain.location.country.Country;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class City {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="city_id")
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    @Enumerated(EnumType.STRING)
    private CityType cityType;

}

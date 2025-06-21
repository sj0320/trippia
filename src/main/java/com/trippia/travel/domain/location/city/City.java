package com.trippia.travel.domain.location.city;

import com.trippia.travel.domain.common.CityType;
import com.trippia.travel.domain.location.country.Country;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    private String imageUrl;

    @Builder
    private City(String name, Country country, CityType cityType, String imageUrl) {
        this.name = name;
        this.country = country;
        this.cityType = cityType;
        this.imageUrl = imageUrl;
    }
}

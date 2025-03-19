package com.trippia.travel.domain.location;

import jakarta.persistence.*;

@Entity
public class City {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="city_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    private String name;


}

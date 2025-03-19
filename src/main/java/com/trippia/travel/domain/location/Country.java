package com.trippia.travel.domain.location;

import jakarta.persistence.*;

@Entity
public class Country {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="country_id")
    private Long id;

    private String name;

}

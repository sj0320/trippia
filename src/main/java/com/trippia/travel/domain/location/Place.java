package com.trippia.travel.domain.location;

import jakarta.persistence.*;

@Entity
public class Place {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="place_id")
    private Long id;

    private String googleMapId;

    private String name;

    private String address;

    private double latitude;

    private double longitude;

    private String category;


}

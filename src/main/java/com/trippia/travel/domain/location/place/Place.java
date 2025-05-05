package com.trippia.travel.domain.location.place;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
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

    @Builder
    private Place(String googleMapId, String name, String address, double latitude, double longitude, String category) {
        this.googleMapId = googleMapId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
    }
}

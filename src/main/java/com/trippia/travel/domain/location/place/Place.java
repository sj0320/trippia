package com.trippia.travel.domain.location.place;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Place {

    @Id
    private String placeId;

    private String name;

    private String address;

    @Builder
    private Place(String placeId, String name, String address) {
        this.placeId = placeId;
        this.name = name;
        this.address = address;
    }
}

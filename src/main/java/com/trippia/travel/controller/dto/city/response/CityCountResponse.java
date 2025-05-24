package com.trippia.travel.controller.dto.city.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class CityCountResponse {

    private Long cityId;
    private Long count;
}

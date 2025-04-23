package com.trippia.travel.exception.city;

import com.trippia.travel.exception.BaseException;

public class CityException extends BaseException {

    public CityException(String fieldName, String code) {
        super(fieldName, code);
    }

    public CityException(String message) {
        super(message);
    }

    public CityException(String fieldName, String code, String message) {
        super(fieldName, code, message);
    }
}

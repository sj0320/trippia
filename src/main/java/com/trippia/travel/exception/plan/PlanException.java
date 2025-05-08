package com.trippia.travel.exception.plan;

import com.trippia.travel.exception.BaseException;

public class PlanException extends BaseException {
    public PlanException(String fieldName, String code, String message) {
        super(fieldName, code, message);
    }

    public PlanException(String fieldName, String code) {
        super(fieldName, code);
    }

    public PlanException(String message) {
        super(message);
    }
}

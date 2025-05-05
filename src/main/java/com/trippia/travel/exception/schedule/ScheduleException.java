package com.trippia.travel.exception.schedule;

import com.trippia.travel.exception.BaseException;

public class ScheduleException extends BaseException {
    public ScheduleException(String fieldName, String code, String message) {
        super(fieldName, code, message);
    }

    public ScheduleException(String fieldName, String code) {
        super(fieldName, code);
    }

    public ScheduleException(String message) {
        super(message);
    }
}

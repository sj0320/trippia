package com.trippia.travel.exception.scheduleitem;

import com.trippia.travel.exception.BaseException;

public class ScheduleItemException extends BaseException {

    public ScheduleItemException(String fieldName, String code, String message) {
        super(fieldName, code, message);
    }

    public ScheduleItemException(String fieldName, String code) {
        super(fieldName, code);
    }

    public ScheduleItemException(String message) {
        super(message);
    }
}

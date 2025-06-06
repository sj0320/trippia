package com.trippia.travel.exception.companionpost;

import com.trippia.travel.exception.BaseException;

public class CompanionPostException extends BaseException {
    public CompanionPostException(String fieldName, String code, String message) {
        super(fieldName, code, message);
    }

    public CompanionPostException(String fieldName, String code) {
        super(fieldName, code);
    }

    public CompanionPostException(String message) {
        super(message);
    }
}

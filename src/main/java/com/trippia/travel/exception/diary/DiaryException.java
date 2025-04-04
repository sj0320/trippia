package com.trippia.travel.exception.diary;

import com.trippia.travel.exception.BaseException;

public class DiaryException extends BaseException {
    public DiaryException(String fieldName, String code) {
        super(fieldName, code);
    }

    public DiaryException(String fieldName, String code, String message) {
        super(fieldName, code, message);
    }

    public DiaryException(String message) {
        super(message);
    }
}

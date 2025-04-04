package com.trippia.travel.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private String fieldName;
    private String code;

    public BaseException(String fieldName, String code, String message){
        super(message);
        this.code = code;
        this.fieldName = fieldName;
    }

    public BaseException(String fieldName, String code){
        this.fieldName = fieldName;
        this.code = code;
    }

    public BaseException(String message){
        super(message);
    }
}

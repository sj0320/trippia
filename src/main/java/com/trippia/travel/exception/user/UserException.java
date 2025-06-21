package com.trippia.travel.exception.user;

import com.trippia.travel.exception.BaseException;

public class UserException extends BaseException {

    public UserException(String fieldName, String code, String message){
        super(fieldName, code, message);
    }

    public UserException(String fieldName, String code){
        super(fieldName, code);
    }

    public UserException(String message){
        super(message);
    }
}

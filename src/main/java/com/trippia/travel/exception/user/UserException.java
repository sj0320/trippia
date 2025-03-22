package com.trippia.travel.exception.user;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException{

    private String field;
    private String code;
    private String message;

    public UserException(String field,String code,String message){
        super(message);
        this.code = code;
        this.message = message;
        this.field = field;
    }

    public UserException(String field, String code){
        this.field = field;
        this.code = code;
    }

    public UserException(String message){
        super(message);
        this.message = message;
    }

}

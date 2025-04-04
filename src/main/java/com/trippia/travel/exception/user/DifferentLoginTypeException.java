package com.trippia.travel.exception.user;

import com.trippia.travel.domain.common.LoginType;
import lombok.Getter;

@Getter
public class DifferentLoginTypeException extends UserException{

    private final LoginType previousType;
    private final LoginType newType;
    private final String email;

    public DifferentLoginTypeException(LoginType previousType, LoginType newType, String email) {
        super("사용자는 이미 " + previousType + "으로 로그인한 적 있습니다.");
        this.previousType = previousType;
        this.newType = newType;
        this.email = email;
    }

}

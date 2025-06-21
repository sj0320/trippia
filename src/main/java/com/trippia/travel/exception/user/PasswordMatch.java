package com.trippia.travel.exception.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchValidator.class)
public @interface PasswordMatch {

    String message() default "비밀번호가 일치하지 않습니다.";

    // 그룹을 위한 속성 (선택 사항)
    Class<?>[] groups() default {};

    // 페이로드를 위한 속성 (선택 사항)
    Class<? extends Payload>[] payload() default {};
}

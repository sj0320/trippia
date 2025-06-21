package com.trippia.travel.exception.user;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, PasswordConfirmable> {
    @Override
    public boolean isValid(PasswordConfirmable request, ConstraintValidatorContext context) {
        if (request == null || request.getNewPassword().equals(request.getConfirmPassword())) {
            return true;
        }

        context.buildConstraintViolationWithTemplate(request.getClass().getAnnotation(PasswordMatch.class).message())
                .addPropertyNode("confirmPassword")
                .addConstraintViolation();
        return false;
    }

}

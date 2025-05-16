package com.trippia.travel.exception.user;

import com.trippia.travel.controller.dto.user.requset.UserSaveRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, UserSaveRequest> {
    @Override
    public boolean isValid(UserSaveRequest saveRequest, ConstraintValidatorContext context) {
        if (saveRequest == null || saveRequest.getPassword().equals(saveRequest.getConfirmPassword())) {
            return true;
        }

        context.buildConstraintViolationWithTemplate(saveRequest.getClass().getAnnotation(PasswordMatch.class).message())
                .addPropertyNode("confirmPassword")
                .addConstraintViolation();
        return false;
    }
}

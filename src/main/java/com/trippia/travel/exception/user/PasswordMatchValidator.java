package com.trippia.travel.exception.user;

import com.trippia.travel.controller.dto.UserDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, UserDto.SaveRequest> {
    @Override
    public boolean isValid(UserDto.SaveRequest saveRequest, ConstraintValidatorContext context) {
        if (saveRequest == null || saveRequest.getPassword().equals(saveRequest.getConfirmPassword())) {
            return true;
        }

        context.buildConstraintViolationWithTemplate(saveRequest.getClass().getAnnotation(PasswordMatch.class).message())
                .addPropertyNode("confirmPassword")
                .addConstraintViolation();
        return false;
    }
}

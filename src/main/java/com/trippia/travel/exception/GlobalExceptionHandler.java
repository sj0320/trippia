package com.trippia.travel.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public String runtimeExceptionHandler(RuntimeException e, Model model){
        model.addAttribute("message", e.getMessage());
        return "error/4xx";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String accessDeniedHandler(AccessDeniedException e) {
        return "error/403";
    }

}

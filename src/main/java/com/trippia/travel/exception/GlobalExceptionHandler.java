package com.trippia.travel.exception;

import com.trippia.travel.exception.plan.PlanException;
import com.trippia.travel.exception.user.UserException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("입력값이 유효하지 않습니다.");
        return ResponseEntity.badRequest().body(errorMessage);
    }

    @ExceptionHandler(UserException.class)
    @ResponseBody
    public ResponseEntity<String> handleUserException(UserException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(PlanException.class)
    @ResponseBody
    public ResponseEntity<String> handlePlanException(PlanException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }


    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

}

package com.trippia.travel.exception;

import com.trippia.travel.exception.companionpost.CompanionPostException;
import com.trippia.travel.exception.plan.PlanException;
import com.trippia.travel.exception.user.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalApiExceptionHandler {

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

    @ExceptionHandler(CompanionPostException.class)
    @ResponseBody
    public ResponseEntity<String> handleCompanionPostException(CompanionPostException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}

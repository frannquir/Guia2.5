package com.example.demo.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> entityNotFound (EntityNotFoundException e) {
       ErrorResponse error = ErrorResponse.builder()
               .error("NOT_FOUND")
               .message(e.getMessage())
               .build();
       return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}

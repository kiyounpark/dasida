package com.bonju.review.exception.handler;


import com.bonju.review.exception.exception.OpenAiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class OpenAiExceptionHandler {

    @ExceptionHandler(OpenAiException.class)
    public ResponseEntity<Map<String, String>> handleOpenAiApiExceptions(OpenAiException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

package com.bonju.review.knowledge.exception;

import com.bonju.review.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class KnowledgeExceptionHandler {

  @ExceptionHandler(KnowledgeException.class)
  public ResponseEntity<ErrorResponse> handleKnowledgeException(KnowledgeException e, HttpServletRequest request) {

    ErrorResponse errorResponse = ErrorResponse.builder()
            .status(e.getHttpStatus().value())
            .error(e.getHttpStatus().getReasonPhrase())
            .message(e.getMessage())
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();

    return ResponseEntity.status(e.getHttpStatus().value()).body(errorResponse);
  }

}

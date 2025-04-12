package com.bonju.review.knowledge.exception;

import com.bonju.review.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class KnowledgeExceptionHandler {
  private static final String ERROR_MESSAGE = "지식 정보를 불러오는 중 문제가 발생했습니다. 관리자에게 문의해주세요.";

  @ExceptionHandler(KnowledgeException.class)
  public ResponseEntity<ErrorResponse> handleKnowledgeException(KnowledgeException e, HttpServletRequest request) {

    ErrorResponse errorResponse = ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .message(ERROR_MESSAGE)
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

}

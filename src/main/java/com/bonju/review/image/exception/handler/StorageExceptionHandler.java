package com.bonju.review.image.exception.handler;

import com.bonju.review.exception.ErrorResponse;
import com.bonju.review.image.exception.exception.StorageException;
import com.bonju.review.slack.SlackErrorMessageFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class StorageExceptionHandler {

  private final SlackErrorMessageFactory slackErrorMessageFactory;

  @ExceptionHandler(StorageException.class)
  public ResponseEntity<ErrorResponse> handleKnowledgeException(StorageException e, HttpServletRequest request) {

    ErrorResponse errorResponse = ErrorResponse.builder()
            .status(e.getHttpStatus().value())
            .error(e.getHttpStatus().getReasonPhrase())
            .message(e.getMessage())
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();

    String errorMessage = slackErrorMessageFactory.createErrorMessage(request, e);
    log.error(errorMessage);

    return ResponseEntity.status(e.getHttpStatus().value()).body(errorResponse);
  }

}

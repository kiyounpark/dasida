package com.bonju.review.devicetoken.exception;

import com.bonju.review.exception.ErrorResponse;
import com.bonju.review.knowledge.exception.KnowledgeException;
import com.bonju.review.slack.SlackErrorMessageFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class DeviceTokenExceptionHandler {

  private final SlackErrorMessageFactory slackErrorMessageFactory;

  @ExceptionHandler(DeviceTokenException.class)
  public ResponseEntity<ErrorResponse> handleDeviceTokenException(DeviceTokenException e, HttpServletRequest request) {
    String errorMessage = slackErrorMessageFactory.createErrorMessage(request, e);
    log.error(errorMessage);


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

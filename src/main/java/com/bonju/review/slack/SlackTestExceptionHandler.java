package com.bonju.review.slack;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Profile("!test")
@RequiredArgsConstructor
public class SlackTestExceptionHandler {

  private final SlackErrorMessageFactory slackErrorMessageFactory;

  @ExceptionHandler(SlackTestException.class)
  public ResponseEntity<String> handleSlackTestException(HttpServletRequest request, SlackTestException e) {
    log.error(slackErrorMessageFactory.createErrorMessage(request, e));
    return ResponseEntity.internalServerError().body(e.getMessage());
  }
}

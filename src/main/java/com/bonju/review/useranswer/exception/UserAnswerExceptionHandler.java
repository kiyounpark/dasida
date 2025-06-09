package com.bonju.review.useranswer.exception;

import com.bonju.review.exception.ErrorResponse;
import com.bonju.review.quiz.exception.exception.QuizException;
import com.bonju.review.slack.SlackErrorMessageFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestControllerAdvice
@Slf4j
public class UserAnswerExceptionHandler {
  private final SlackErrorMessageFactory slackErrorMessageFactory;

  @ExceptionHandler(UserAnswerException.class)
  public ResponseEntity<ErrorResponse> handleKnowledgeException(UserAnswerException e, HttpServletRequest request) {
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

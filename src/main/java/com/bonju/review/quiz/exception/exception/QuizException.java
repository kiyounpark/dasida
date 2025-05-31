package com.bonju.review.quiz.exception.exception;

import com.bonju.review.quiz.exception.errorcode.QuizErrorCode;
import org.springframework.http.HttpStatus;

public class QuizException extends RuntimeException{
  private final QuizErrorCode errorCode;

  public QuizException(QuizErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.errorCode = errorCode;
  }

  public QuizException(QuizErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
  public HttpStatus getHttpStatus() {
    return errorCode.getHttpStatus();
  }
}



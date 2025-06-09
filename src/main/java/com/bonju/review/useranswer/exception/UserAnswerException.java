package com.bonju.review.useranswer.exception;

import org.springframework.http.HttpStatus;

public class UserAnswerException extends RuntimeException{
  private final UserAnswerErrorCode errorCode;

  public UserAnswerException(UserAnswerErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.errorCode = errorCode;
  }

  public UserAnswerException(UserAnswerErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
  public HttpStatus getHttpStatus() {
    return errorCode.getHttpStatus();
  }
}

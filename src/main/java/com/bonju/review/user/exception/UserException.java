package com.bonju.review.user.exception;

import org.springframework.http.HttpStatus;


public class UserException extends RuntimeException {
  private final UserErrorCode errorCode;

  public UserException(UserErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.errorCode = errorCode;
  }

  public UserException(UserErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
  public HttpStatus getHttpStatus() {
    return errorCode.getHttpStatus();
  }
}



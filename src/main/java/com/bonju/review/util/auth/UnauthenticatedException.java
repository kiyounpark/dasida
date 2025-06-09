package com.bonju.review.util.auth;

import org.springframework.http.HttpStatus;

public class UnauthenticatedException extends RuntimeException {
  private final AuthErrorCode errorCode;

  public UnauthenticatedException(AuthErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.errorCode = errorCode;
  }

  public UnauthenticatedException(AuthErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
  public HttpStatus getHttpStatus() {
    return errorCode.getHttpStatus();
  }
}

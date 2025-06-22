package com.bonju.review.notification.exception;

import org.springframework.http.HttpStatus;

public class FcmException extends RuntimeException {
  private final FcmErrorCode errorCode;

  public FcmException(FcmErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.errorCode = errorCode;
  }

  public FcmException(FcmErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
  public HttpStatus getHttpStatus() {
    return errorCode.getHttpStatus();
  }
}

package com.bonju.review.image.exception.exception;

import com.bonju.review.image.exception.errorcode.StorageErrorCode;
import org.springframework.http.HttpStatus;

public class StorageException extends RuntimeException {

  private final StorageErrorCode errorCode;

  public StorageException(StorageErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.errorCode = errorCode;
  }

  public StorageException(StorageErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public HttpStatus getHttpStatus() {
    return errorCode.getHttpStatus();
  }
}

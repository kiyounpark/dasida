package com.bonju.review.s3.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ImageUploadException extends RuntimeException {

  private final ImageUploadErrorCode errorCode;

  public ImageUploadException(ImageUploadErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public ImageUploadException(ImageUploadErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.errorCode = errorCode;
  }

  public HttpStatus getHttpStatus() {
    return errorCode.getHttpStatus();
  }
}

package com.bonju.review.image.exception.exception;

import com.bonju.review.image.exception.errorcode.ImageErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ImageException extends RuntimeException {

  private final ImageErrorCode errorCode;

  public ImageException(ImageErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public HttpStatus getHttpStatus() {
    return errorCode.getHttpStatus();
  }
}

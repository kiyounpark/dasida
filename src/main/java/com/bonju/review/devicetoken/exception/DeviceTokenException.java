package com.bonju.review.devicetoken.exception;

import com.bonju.review.util.enums.error_code.KnowledgeErrorCode;
import lombok.Getter;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;

@Getter
public class DeviceTokenException extends RuntimeException {

  private final DeviceTokenErrorCode errorCode;

  public DeviceTokenException(DeviceTokenErrorCode errorCode, DataAccessException e) {
    super(errorCode.getMessage(), e);
    this.errorCode = errorCode;
  }

    public DeviceTokenException(DeviceTokenErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public HttpStatus getHttpStatus() {
    return errorCode.getHttpStatus();
  }
}

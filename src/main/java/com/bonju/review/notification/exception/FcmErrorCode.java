package com.bonju.review.notification.exception;

import com.bonju.review.util.ApiErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum FcmErrorCode implements ApiErrorCode {
  PUSH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "오늘의 퀴즈 알람을 전송하는 도중 오류가 발생하였습니다.");

  private final HttpStatus httpStatus;
  private final String message;

  FcmErrorCode(HttpStatus httpStatus, String message) {
    this.httpStatus = httpStatus;
    this.message = message;
  }
}

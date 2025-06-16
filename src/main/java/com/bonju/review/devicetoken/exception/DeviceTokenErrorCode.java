package com.bonju.review.devicetoken.exception;

import com.bonju.review.util.ApiErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Getter
public enum DeviceTokenErrorCode implements ApiErrorCode {
  DB_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "DeviceToken 을 DB에 저장 또는 불러오던 도중 에러가 발생하였습니다.");

  private final HttpStatus httpStatus;
  private final String message;
}

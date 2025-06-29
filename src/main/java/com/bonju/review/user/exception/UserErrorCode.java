package com.bonju.review.user.exception;

import com.bonju.review.util.ApiErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum UserErrorCode implements ApiErrorCode {
  COUNT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "전체 유저에 인원수를 파악하다 에러가 발생하였습니다.");

  private final HttpStatus httpStatus;
  private final String message;
}

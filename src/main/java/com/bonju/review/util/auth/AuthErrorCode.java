package com.bonju.review.util.auth;

import com.bonju.review.util.ApiErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ApiErrorCode {

  UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "로그인 정보가 유효하지 않습니다."),
  USER_NOT_FOUND (HttpStatus.NOT_FOUND,     "유저가 DB에 존재하지 않습니다.");

  private final HttpStatus httpStatus;
  private final String message;
}

package com.bonju.review.image.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ImageErrorCode {
  INVALID_EXTENSION(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 확장자입니다"),
  INVALID_MIME_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 MIME 타입입니다"),
  FILE_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "이미지 용량이 너무 큽니다");

  private final HttpStatus httpStatus;
  private final String message;
}

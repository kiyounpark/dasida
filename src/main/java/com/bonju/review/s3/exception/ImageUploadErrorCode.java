package com.bonju.review.s3.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ImageUploadErrorCode {
  UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드 및 조회 중 오류가 발생했습니다"),
  INVALID_FORMAT(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 형식입니다"),
  FILE_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "이미지 용량이 너무 큽니다");

  private final HttpStatus httpStatus;
  private final String message;
}

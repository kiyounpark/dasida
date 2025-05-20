package com.bonju.review.image.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum StorageErrorCode {

  /** 이미지 업로드 중 오류가 발생했을 때 */
  UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드 오류가 발생했습니다"),

  /** 이미지 조회(Public URL 생성 등) 중 오류가 발생했을 때 */
  RETRIEVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 조회 오류가 발생했습니다");

  private final HttpStatus httpStatus;
  private final String message;
}

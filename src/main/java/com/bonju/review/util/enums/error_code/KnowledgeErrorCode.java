package com.bonju.review.util.enums.error_code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum KnowledgeErrorCode {
  NOT_FOUND(HttpStatus.NOT_FOUND, "지식을 찾을 수 없습니다"),
  RETRIEVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "지식 조회 중 오류가 발생했습니다"),
  REGISTER_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "지식 DB에 저장중 오류가 발생했습니다");
  private final HttpStatus httpStatus;
  private final String message;
}

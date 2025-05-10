package com.bonju.review.util.enums.error_code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum KnowledgeErrorCode {
  NOT_FOUND(HttpStatus.NOT_FOUND, "지식을 찾을 수 없습니다");

  private final HttpStatus httpStatus;
  private final String message;
}

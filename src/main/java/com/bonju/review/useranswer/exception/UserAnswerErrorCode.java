package com.bonju.review.useranswer.exception;

import com.bonju.review.util.ApiErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserAnswerErrorCode implements ApiErrorCode {
  DB_SAVE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "DB에 문제가 발생하여 UserAnswer(퀴즈 풀이) 저장에 실패하였습니다.");

  private final HttpStatus httpStatus;
  private final String message;
}

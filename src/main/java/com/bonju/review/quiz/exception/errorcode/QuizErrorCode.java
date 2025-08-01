package com.bonju.review.quiz.exception.errorcode;

import com.bonju.review.util.ApiErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum QuizErrorCode implements ApiErrorCode {
  /**
   * 퀴즈 생성 과정에서 OpenAI 호출 실패 시 사용
   */
  QUIZ_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "퀴즈 생성 중 오류가 발생했습니다"),

  /**
   * AI가 생성한 퀴즈를 VO로 매핑하는 과정에서 오류 발생 시 사용
   */
  QUIZ_MAPPING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AI가 생성한 퀴즈를 VO로 매핑 중 오류가 발생했습니다"),
  QUIZ_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "퀴즈를 DB에 저장하는 도중 문제가 발생하였습니다."),

  QUIZ_MIME_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지를 MIME type 으로 반환 중 오류가 발생했습니다."),
  QUIZ_TODAY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "오늘의 퀴즈를 불러오는 도중 오류가 발생하였습니다."),
  QUIZ_FIND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "퀴즈를 불러오는 도중 오류가 발생하였습니다.");

  private final HttpStatus httpStatus;
  private final String message;

  QuizErrorCode(HttpStatus httpStatus, String message) {
    this.httpStatus = httpStatus;
    this.message = message;
  }
}

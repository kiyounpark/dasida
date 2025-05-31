package com.bonju.review.quiz.mapper;

import com.bonju.review.quiz.exception.errorcode.QuizErrorCode;
import com.bonju.review.quiz.exception.exception.QuizException;
import com.bonju.review.quiz.vo.QuizCreationData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AI가 생성한 JSON 문자열을 QuizCreationData 리스트로 매핑합니다.
 */
@Component
public class QuizGenerationMapper {

  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * JSON 배열 형태의 퀴즈 데이터를 QuizCreationData 객체 리스트로 변환합니다.
   *
   * @param quizJson AI가 생성한 JSON 문자열
   * @return QuizCreationData 객체 리스트
   * @throws QuizException 매핑 중 오류가 발생한 경우
   */
  public List<QuizCreationData> mapFrom(String quizJson) {
    try {
      return objectMapper.readValue(
              quizJson,
              new TypeReference<>() {
              }
      );
    } catch (Exception e) {
      throw new QuizException(QuizErrorCode.QUIZ_MAPPING_FAILED, e);
    }
  }
}

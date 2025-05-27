package com.bonju.review.quiz.mapper;

import com.bonju.review.quiz.exception.QuizErrorCode;
import com.bonju.review.quiz.exception.QuizException;
import com.bonju.review.quiz.vo.QuizCreationData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("QuizGenerationMapper 테스트")
class QuizGenerationMapperTest {

  private final QuizGenerationMapper mapper = new QuizGenerationMapper();

  @Test
  @DisplayName("매핑된 리스트 크기가 올바른지 검증한다")
  void mapFrom_validJson_returnsCorrectSize() {
    // given
    String json = """
            [
              {"question":"Q1","answer":"A1","hint":"H1"},
              {"question":"Q2","answer":"A2","hint":"H2"},
              {"question":"Q3","answer":"A3","hint":"H3"}
            ]
            """;

    // when
    List<QuizCreationData> result = mapper.mapFrom(json);

    // then
    assertThat(result).hasSize(3);
  }

  @Test
  @DisplayName("매핑된 각각의 QuizCreationData 내용이 올바른지 검증한다")
  void mapFrom_validJson_returnsCorrectContent() {
    // given
    String json = """
            [
              {"question":"Q1","answer":"A1","hint":"H1"}
            ]
            """;

    // when
    List<QuizCreationData> result = mapper.mapFrom(json);
    QuizCreationData creationData = result.getFirst();
    // then

    assertThat(creationData.question()).isEqualTo("Q1");
    assertThat(creationData.answer()).isEqualTo("A1");
    assertThat(creationData.hint()).isEqualTo("H1");
  }

  @Test
  @DisplayName("잘못된 JSON 입력 시 예외를 던진다")
  void mapFrom_invalidJson_throwsException() {
    // given
    String invalidJson = "not a json";

    // when & then
    assertThatThrownBy(() -> mapper.mapFrom(invalidJson))
            .isInstanceOf(QuizException.class)
            .hasMessage(QuizErrorCode.QUIZ_MAPPING_FAILED.getMessage());
  }
}


package com.bonju.review.quiz.service;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.quiz.client.AiClient;
import com.bonju.review.quiz.exception.errorcode.QuizErrorCode;
import com.bonju.review.quiz.exception.exception.QuizException;
import com.bonju.review.quiz.mapper.QuizGenerationMapper;
import com.bonju.review.quiz.repository.QuizRepository;
import com.bonju.review.quiz.service.register.QuizAutoGeneratorServiceImpl;
import com.bonju.review.quiz.vo.QuizCreationData;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

/**
 * QuizAutoGeneratorServiceImpl 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class QuizAutoGeneratorServiceTest {

  private static final String MOCK_QUIZ_JSON_DATA = """
      [
        {
          "question": "1 + 1은?",
          "answer": "2",
          "hint": "초등학교 1학년 문제"
        }
      ]
      """;

  @Mock
  private AiClient openAiClient;

  @Mock
  private QuizGenerationMapper quizGenerationMapper;

  @Mock
  private QuizRepository quizRepository;

  @Mock
  private UserService userService;

  @InjectMocks
  private QuizAutoGeneratorServiceImpl quizAutoGeneratorService;

  @Nested
  @DisplayName("정상 흐름")
  class SuccessCases {

    @Test
    @DisplayName("AI 호출 결과가 매핑되어 반환된다")
    void shouldReturnParsedQuizzes() {
      // given
      Knowledge knowledge = Knowledge.builder().content("지식 내용").build();
      User user = User.builder().build();
      List<QuizCreationData> generatedQuizzes = List.of(
              QuizCreationData.builder().question("Q1").answer("A1").hint("H1").build(),
              QuizCreationData.builder().question("Q2").answer("A2").hint("H2").build(),
              QuizCreationData.builder().question("Q3").answer("A3").hint("H3").build()
      );

      given(userService.findUser()).willReturn(user);
      given(openAiClient.generateRawQuizJson(anyString(), anyList()))
              .willReturn(MOCK_QUIZ_JSON_DATA);
      given(quizGenerationMapper.mapFrom(MOCK_QUIZ_JSON_DATA))
              .willReturn(generatedQuizzes);
      willDoNothing().given(quizRepository).saveAll(anyList());

      // when
      List<QuizCreationData> result =
              quizAutoGeneratorService.generateQuiz(knowledge);

      // then
      assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("매핑된 QuizCreationData의 필드가 올바르게 반환된다")
    void shouldCorrectlyMapQuizFields() {
      // given
      Knowledge knowledge = Knowledge.builder().content("지식 내용").build();
      User user = User.builder().build();
      QuizCreationData dto = QuizCreationData.builder()
              .question("Q1").answer("A1").hint("H1").build();

      given(userService.findUser()).willReturn(user);
      given(openAiClient.generateRawQuizJson(anyString(), anyList()))
              .willReturn(MOCK_QUIZ_JSON_DATA);
      given(quizGenerationMapper.mapFrom(MOCK_QUIZ_JSON_DATA))
              .willReturn(List.of(dto));
      willDoNothing().given(quizRepository).saveAll(anyList());

      // when
      List<QuizCreationData> result =
              quizAutoGeneratorService.generateQuiz(knowledge);

      // then
      QuizCreationData actual = result.getFirst();
      assertThat(actual.question()).isEqualTo("Q1");
      assertThat(actual.answer()).isEqualTo("A1");
      assertThat(actual.hint()).isEqualTo("H1");
    }
  }

  @Nested
  @DisplayName("예외 처리")
  class ExceptionCases {

    @Test
    @DisplayName("저장 중 DataAccessException 발생 시 QuizException으로 래핑된다")
    void wrapsQuizExceptionOnSaveFailure() {
      // given
      Knowledge knowledge = Knowledge.builder().content("지식 내용").build();
      User user = User.builder().build();
      QuizCreationData dto = QuizCreationData.builder()
              .question("Q1").answer("A1").hint("H1").build();

      given(userService.findUser()).willReturn(user);
      given(openAiClient.generateRawQuizJson(anyString(), anyList()))
              .willReturn(MOCK_QUIZ_JSON_DATA);
      given(quizGenerationMapper.mapFrom(MOCK_QUIZ_JSON_DATA))
              .willReturn(List.of(dto));
      willThrow(new DataAccessException("DB 오류") {})
              .given(quizRepository).saveAll(anyList());

      // when / then
      assertThatThrownBy(() ->
              quizAutoGeneratorService.generateQuiz(knowledge)
      )
              .isInstanceOf(QuizException.class)
              .extracting("errorCode")
              .isEqualTo(QuizErrorCode.QUIZ_SAVE_FAILED);
    }
  }
}

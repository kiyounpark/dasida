package com.bonju.review.quiz;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.quiz.client.AiClient;
import com.bonju.review.quiz.mapper.QuizGenerationMapper;
import com.bonju.review.quiz.repository.QuizAutoGenerationRepository;
import com.bonju.review.quiz.service.QuizAutoGeneratorServiceImpl;
import com.bonju.review.quiz.vo.QuizCreationData;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

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
  AiClient openAiClient;

  @Mock
  QuizGenerationMapper quizGenerationMapper;

  @Mock
  QuizAutoGenerationRepository quizAutoGenerationRepository;

  @Mock
  UserService userService;

  @InjectMocks
  QuizAutoGeneratorServiceImpl quizAutoGeneratorService;

  @Test
  @DisplayName("Ai 에서 생성된 JSON이 올바르게 파싱되어 3개의 퀴즈가 반환된다.")
  void shouldReturnThreeParsedQuizzes() {
    // given
    String content = "지식 내용";

    List<QuizCreationData> generatedQuizzes = List.of(
            QuizCreationData.builder().question("Q1").answer("A1").hint("H1").build(),
            QuizCreationData.builder().question("Q2").answer("A2").hint("H2").build(),
            QuizCreationData.builder().question("Q3").answer("A3").hint("H3").build()
    );
    given(userService.findUser()).willReturn(User.builder().build());
    given(openAiClient.generateRawQuizJson(anyString(), anyList())).willReturn(MOCK_QUIZ_JSON_DATA);
    willDoNothing().given(quizAutoGenerationRepository).saveAll(anyList());
    given(quizGenerationMapper.mapFrom(MOCK_QUIZ_JSON_DATA)).willReturn(generatedQuizzes);

    // when
    List<QuizCreationData> quizCreationDataList = quizAutoGeneratorService.generateQuiz(Knowledge.builder().build(), content);

    // then
    assertThat(quizCreationDataList).hasSize(3);
  }

  @Test
  @DisplayName("생성된 퀴즈의 질문/답변/힌트가 올바르게 매핑된다.")
  void shouldCorrectlyMapQuizFields() {
    // given
    String content = "지식 내용";

    String question = "Q1";
    String answer = "A1";
    String hint = "H1";
    List<QuizCreationData> generatedQuizzes = List.of(
            QuizCreationData.builder().question(question).answer(answer).hint(hint).build()
    );

    given(userService.findUser()).willReturn(User.builder().build());
    given(openAiClient.generateRawQuizJson(anyString(), anyList())).willReturn(MOCK_QUIZ_JSON_DATA);
    willDoNothing().given(quizAutoGenerationRepository).saveAll(anyList());
    given(quizGenerationMapper.mapFrom(MOCK_QUIZ_JSON_DATA)).willReturn(generatedQuizzes);

    // when
    List<QuizCreationData> quizCreationDataList = quizAutoGeneratorService.generateQuiz(Knowledge.builder().build(), content);
    QuizCreationData creationData = quizCreationDataList.getFirst();

    // then
    assertThat(creationData.question()).isEqualTo(question);
    assertThat(creationData.answer()).isEqualTo(answer);
    assertThat(creationData.hint()).isEqualTo(hint);
  }
}

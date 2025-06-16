package com.bonju.review.wrongnote;

import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.useranswer.entity.UserAnswer;
import com.bonju.review.useranswer.service.UserAnswerService;
import com.bonju.review.wronganswernote.dto.WrongAnswerGroupResponseDto;
import com.bonju.review.wronganswernote.service.WrongNoteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class WrongNoteServiceTest {

  private static final int DAY_TYPE = 0;

  @Mock   UserAnswerService userAnswerService;   // UserAnswer 조회를 Mock 처리
  @InjectMocks WrongNoteService wrongNoteService; // 테스트 대상

  @Test
  @DisplayName("오답이 한 번이라도 기록된 퀴즈만 그룹핑하여 반환한다")
  void returns_only_quizzes_that_have_any_wrong_answer() {
    // given
    Quiz quiz1 = createQuiz(101L, "Q1", "A1");
    Quiz quiz2 = createQuiz(202L, "Q2", "A2");
    Quiz quiz3 = createQuiz(303L, "Q3", "A3");

    // quiz1 – 정답·오답 모두 존재
    UserAnswer q1Right = createUserAnswer(quiz1, true);
    UserAnswer q1Wrong = createUserAnswer(quiz1, false);

    // quiz2 – 오답만 존재
    UserAnswer q2Wrong = createUserAnswer(quiz2, false);

    // quiz3 – 정답만 존재 (오답 노트에 포함 ❌)
    UserAnswer q3Right = createUserAnswer(quiz3, true);

    // Mock 스텁: 서비스가 반환할 리스트 지정
    given(userAnswerService.findAll())
            .willReturn(List.of(q1Right, q1Wrong, q2Wrong, q3Right));

    // when
    List<WrongAnswerGroupResponseDto> groups = wrongNoteService.loadWrongNote();

    // then
    // 1) 오답이 없는 quiz3(303) 은 결과에 포함되지 않는다
    assertThat(groups)
            .extracting(WrongAnswerGroupResponseDto::getQuizId)
            .containsExactlyInAnyOrder(101L, 202L);

    // 2) quiz1 그룹 안에는 정답·오답 모두 2개 존재
    WrongAnswerGroupResponseDto quiz1Group = groups.stream()
            .filter(g -> g.getQuizId().equals(101L))
            .findFirst().orElseThrow();

    assertThat(quiz1Group.getAnswers())
            .hasSize(2)
            .extracting("correct")
            .containsExactlyInAnyOrder(true, false);
  }

  @Test
  @DisplayName("사용자 답안이 없으면 빈 리스트를 반환한다")
  void returns_empty_list_when_no_answers() {
    // given
    given(userAnswerService.findAll()).willReturn(List.of());

    // when
    List<WrongAnswerGroupResponseDto> groups = wrongNoteService.loadWrongNote();

    // then
    assertThat(groups).isEmpty();
  }

  /* ──── 헬퍼 메서드 : 테스트 객체 생성 ──── */

  private static Quiz createQuiz(Long id, String question, String answer) {
    Quiz quiz = Quiz.builder()
            .question(question)
            .answer(answer)
            .build();
    // PK(id) 를 리플렉션으로 주입하여 영속성 없이도 식별값 고정
    ReflectionTestUtils.setField(quiz, "id", id);
    return quiz;
  }

  private static UserAnswer createUserAnswer(Quiz quiz, boolean correct) {
    return UserAnswer.builder()
            .quiz(quiz)
            .dayType(DAY_TYPE)
            .isCorrect(correct)
            .build();
  }
}
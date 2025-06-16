package com.bonju.review.quiz.service;

import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.useranswer.entity.UserAnswer;
import com.bonju.review.quiz.service.find.QuizFindService;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import com.bonju.review.useranswer.command.SubmitUserAnswerCommand;
import com.bonju.review.useranswer.exception.UserAnswerException;
import com.bonju.review.useranswer.repository.UserAnswerRepository;
import com.bonju.review.useranswer.service.UserAnswerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("사용자 답안 제출 서비스 단위 테스트")
class UserAnswerServiceTest {

  private static final long   QUIZ_ID       = 1L;
  private static final int    DAY_TYPE      = 0;
  private static final String CORRECT_ANSWER = "정답";
  private static final String WRONG_ANSWER   = "오답";

  @Mock QuizFindService      quizFindService;
  @Mock UserService          userService;
  @Mock
  UserAnswerRepository userAnswerRepository;

  @InjectMocks
  UserAnswerService userAnswerService;

  /* ---------- 정상 흐름 ---------- */

  @Test
  @DisplayName("정답을 제출하면 isCorrect가 true인 응답을 반환한다")
  void whenCorrectAnswer_thenReturnTrue() {
    // given
    User user = mockUser();
    Quiz quiz = mockQuiz(CORRECT_ANSWER);
    SubmitUserAnswerCommand cmd = mockCommand(CORRECT_ANSWER);

    given(userService.findUser()).willReturn(user);
    given(quizFindService.findQuizByIdAndUser(QUIZ_ID, user)).willReturn(quiz);
    willDoNothing().given(userAnswerRepository).save(any(UserAnswer.class));

    // when
    var result = userAnswerService.submitAnswer(cmd);

    // then
    assertThat(result.isCorrect()).isTrue();
  }

  @Test
  @DisplayName("오답을 제출하면 isCorrect가 false인 응답을 반환한다")
  void whenWrongAnswer_thenReturnFalse() {
    // given
    User user = mockUser();
    Quiz quiz = mockQuiz(CORRECT_ANSWER);
    SubmitUserAnswerCommand cmd = mockCommand(WRONG_ANSWER);

    given(userService.findUser()).willReturn(user);
    given(quizFindService.findQuizByIdAndUser(QUIZ_ID, user)).willReturn(quiz);
    willDoNothing().given(userAnswerRepository).save(any(UserAnswer.class));

    // when
    var result = userAnswerService.submitAnswer(cmd);

    // then
    assertThat(result.isCorrect()).isFalse();
  }

  /* ---------- 예외 흐름 ---------- */

  @Test
  @DisplayName("DB 저장 중 오류가 발생하면 UserAnswerException을 던진다")
  void whenSaveFails_thenThrowUserAnswerException() {
    // given
    User user = mockUser();
    Quiz quiz = mockQuiz(CORRECT_ANSWER);
    SubmitUserAnswerCommand cmd = mockCommand(WRONG_ANSWER);

    given(userService.findUser()).willReturn(user);
    given(quizFindService.findQuizByIdAndUser(QUIZ_ID, user)).willReturn(quiz);
    willThrow(new DataAccessException("저장 실패") { })
            .given(userAnswerRepository).save(any(UserAnswer.class));

    // then
    assertThatThrownBy(() -> userAnswerService.submitAnswer(cmd))
            .isInstanceOf(UserAnswerException.class);
  }

  @Test
  @DisplayName("DB 에서 UserAnswer 조회중 오류가 발생하면 UserAnswerException을 던진다")
  void whenSaveFails_thenThrowUserAnswerException1() {
    // given
    User user = User.builder()
            .build();
    given(userService.findUser()).willReturn(user);
    willThrow(new DataAccessException("저장 실패") { })
            .given(userAnswerRepository).findAll(user);

    // then
    assertThatThrownBy(() -> userAnswerService.findAll())
            .isInstanceOf(UserAnswerException.class);
  }


  /* ---------- Fixture ---------- */

  private static SubmitUserAnswerCommand mockCommand(String answer) {
    return new SubmitUserAnswerCommand(QUIZ_ID, answer, DAY_TYPE);
  }

  private static User mockUser() {
    return User.builder().kakaoId("123").build();
  }

  private static Quiz mockQuiz(String answer) {
    return Quiz.builder().answer(answer).build();
  }
}
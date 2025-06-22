package com.bonju.review.quiz.service.find;

import com.bonju.review.quiz.exception.errorcode.QuizErrorCode;
import com.bonju.review.quiz.exception.exception.QuizException;
import com.bonju.review.quiz.repository.find.QuizFindRepository;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class QuizFindServiceImplTest {

  @Mock
  QuizFindRepository quizFindRepository;

  @Mock
  UserService userService;
  @InjectMocks   QuizFindServiceImpl quizFindService;

  /* ─────────── 존재하는 경우 ─────────── */
  @Test
  @DisplayName("유저 기준 조회 시 퀴즈가 존재하면 true를 반환한다")
  void hasQuiz_returnsTrue_whenExists() {
    // given
    User user = User.builder().build();
    given(userService.findUser()).willReturn(user);
    given(quizFindRepository.isQuizListEmptyByUser(user)).willReturn(true);

    // when
    boolean actual = quizFindService.hasQuizByUser();

    // then
    assertThat(actual).isTrue();
    verify(quizFindRepository).isQuizListEmptyByUser(user);
  }

  /* ─────────── 존재하지 않는 경우 ─────────── */
  @Test
  @DisplayName("유저 기준 조회 시 퀴즈가 없으면 false를 반환한다")
  void hasQuiz_returnsFalse_whenNotExists() {
    // given
    User user = User.builder().build();
    given(userService.findUser()).willReturn(user);
    given(quizFindRepository.isQuizListEmptyByUser(user)).willReturn(false);

    // when
    boolean actual = quizFindService.hasQuizByUser();

    // then
    assertThat(actual).isFalse();
    verify(quizFindRepository).isQuizListEmptyByUser(user);
  }

  /* ─────────── DB 계층 예외 래핑 검증 ─────────── */
  @Test
  @DisplayName("레포지토리가 DataAccessException 을 던지면 QuizException(QUIZ_FIND_FAILED) 으로 변환된다")
  void hasQuiz_dbFail_wrapsQuizException() {
    // given
    User user = User.builder().build();
    given(userService.findUser()).willReturn(user);
    willThrow(new DataAccessResourceFailureException("DB down"))
            .given(quizFindRepository).isQuizListEmptyByUser(user);

    // when & then
    assertThatThrownBy(() -> quizFindService.hasQuizByUser())
            .isInstanceOf(QuizException.class)
            .hasFieldOrPropertyWithValue("errorCode", QuizErrorCode.QUIZ_FIND_FAILED);

    verify(quizFindRepository).isQuizListEmptyByUser(user);
  }
}
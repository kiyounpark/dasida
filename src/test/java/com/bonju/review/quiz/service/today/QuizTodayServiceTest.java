package com.bonju.review.quiz.service.today;

import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.quiz.exception.errorcode.QuizErrorCode;
import com.bonju.review.quiz.exception.exception.QuizException;
import com.bonju.review.quiz.repository.QuizTodayRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class QuizTodayServiceTest {

  @Mock QuizTodayRepository repo;
  @InjectMocks QuizTodayService service;

  @Test
  @DisplayName("성공: 오늘 퀴즈 엔티티 목록을 반환한다")
  void findTodayQuizzes_success() {
    // given
    List<Long> ids = List.of(1L, 2L);
    List<Quiz> quizzes = List.of(Quiz.builder().build(), Quiz.builder().build());

    given(repo.findTodayQuizIds()).willReturn(ids);
    given(repo.findByIdsWithUser(ids)).willReturn(quizzes);

    // when / then
    assertThat(service.findTodayQuizList()).isEqualTo(quizzes);
  }

  @Test
  @DisplayName("레포지토리 예외 시 QuizException 으로 래핑된다")
  void findTodayQuizzes_wraps_exception() {
    given(repo.findTodayQuizIds()).willThrow(new DataAccessResourceFailureException("boom"));

    assertThatThrownBy(() -> service.findTodayQuizList())
            .isInstanceOf(QuizException.class)
            .extracting("errorCode")
            .isEqualTo(QuizErrorCode.QUIZ_TODAY_FAILED);
  }
}
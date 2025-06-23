package com.bonju.review.quiz.service.today;

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

  @Mock QuizTodayRepository quizTodayRepository;
  @InjectMocks QuizTodayService quizTodayService;

  @Test
  @DisplayName("findTodayQuizIds() 성공 시 – 레포지토리 결과를 그대로 반환한다")
  void findTodayQuizIds_returns_repository_result() {
    List<Long> expected = List.of(1L, 2L);

    given(quizTodayRepository.findTodayQuizIds()).willReturn(expected);

    List<Long> actual = quizTodayService.findTodayQuizIds();

    assertThat(actual).containsExactlyElementsOf(expected);
  }

  @Test
  @DisplayName("findTodayQuizIds() 실패 시 – DataAccessException을 QuizException으로 래핑한다")
  void findTodayQuizIds_wraps_DataAccessException() {
    given(quizTodayRepository.findTodayQuizIds())
            .willThrow(new DataAccessResourceFailureException("boom"));

    assertThatThrownBy(() -> quizTodayService.findTodayQuizIds())
            .isInstanceOf(QuizException.class)
            .extracting("errorCode")
            .isEqualTo(QuizErrorCode.QUIZ_TODAY_FAILED);
  }
}
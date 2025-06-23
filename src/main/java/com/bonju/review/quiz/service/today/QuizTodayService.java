package com.bonju.review.quiz.service.today;

import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.quiz.exception.errorcode.QuizErrorCode;
import com.bonju.review.quiz.exception.exception.QuizException;
import com.bonju.review.quiz.repository.QuizTodayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizTodayService {

  private final QuizTodayRepository quizTodayRepository;

  /** Quiz 엔티티 + 작성자(User) 모두 */
  public List<Quiz> findTodayQuizList() {
    try {
      List<Long> ids = quizTodayRepository.findTodayQuizIds();
      return quizTodayRepository.findByIdsWithUser(ids);
    } catch (DataAccessException e) {
      throw new QuizException(QuizErrorCode.QUIZ_TODAY_FAILED, e);
    }
  }
}

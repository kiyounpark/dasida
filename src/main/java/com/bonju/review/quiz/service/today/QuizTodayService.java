package com.bonju.review.quiz.service.today;

import com.bonju.review.quiz.exception.errorcode.QuizErrorCode;
import com.bonju.review.quiz.exception.exception.QuizException;
import com.bonju.review.quiz.repository.QuizTodayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizTodayService {

  private final QuizTodayRepository quizTodayRepository;

  public List<Long> findTodayQuizIds(){
    try{
      return quizTodayRepository.findTodayQuizIds();
    }
    catch (DataAccessException e){
      throw new QuizException(QuizErrorCode.QUIZ_TODAY_FAILED, e);
    }
  }
}

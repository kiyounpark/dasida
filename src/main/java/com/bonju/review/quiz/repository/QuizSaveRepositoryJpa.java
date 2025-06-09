package com.bonju.review.quiz.repository;

import com.bonju.review.quiz.entity.Quiz;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class QuizSaveRepositoryJpa implements QuizRepository {

  private final EntityManager em;
  @Override
  public void saveAll(List<Quiz> quizList) {
    quizList.forEach(em::persist);
  }
}

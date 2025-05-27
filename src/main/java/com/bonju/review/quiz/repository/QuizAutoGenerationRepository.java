package com.bonju.review.quiz.repository;

import com.bonju.review.quiz.entity.Quiz;

import java.util.List;

public interface QuizAutoGenerationRepository {
  void saveAll(List<Quiz> quizList);
}

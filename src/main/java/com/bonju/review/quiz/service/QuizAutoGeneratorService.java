package com.bonju.review.quiz.service;

import com.bonju.review.quiz.vo.QuizCreationData;

import java.util.List;

public interface QuizAutoGeneratorService {
  List<QuizCreationData> generateQuiz(String content);
}

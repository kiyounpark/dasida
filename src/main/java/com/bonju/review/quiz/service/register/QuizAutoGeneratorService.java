package com.bonju.review.quiz.service.register;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.quiz.vo.QuizCreationData;

import java.util.List;

public interface QuizAutoGeneratorService {
  List<QuizCreationData> generateQuiz(Knowledge knowledge);
}

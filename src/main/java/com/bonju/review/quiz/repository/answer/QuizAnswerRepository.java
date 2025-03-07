package com.bonju.review.quiz.repository.answer;

import com.bonju.review.quiz.entity.UserAnswer;

public interface QuizAnswerRepository {
    void save(UserAnswer userAnswer);
}

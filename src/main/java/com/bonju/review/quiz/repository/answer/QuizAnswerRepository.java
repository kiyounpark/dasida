package com.bonju.review.quiz.repository.answer;

import com.bonju.review.useranswer.entity.UserAnswer;

public interface QuizAnswerRepository {
    void save(UserAnswer userAnswer);
}

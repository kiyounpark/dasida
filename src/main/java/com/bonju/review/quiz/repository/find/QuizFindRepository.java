package com.bonju.review.quiz.repository.find;

import com.bonju.review.quiz.entity.Quiz;

public interface QuizFindRepository {
    Quiz findById(Long id);
}

package com.bonju.review.repository;

import com.bonju.review.entity.Quiz;

public interface QuizFindRepository {
    Quiz findById(Long id);
}

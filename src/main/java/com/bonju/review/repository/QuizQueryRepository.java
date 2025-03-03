package com.bonju.review.repository;

import com.bonju.review.entity.Quiz;

import java.util.List;

public interface QuizQueryRepository {

    List<Quiz> findQuizzesCreatedWithin30DaysByUser(Long userId);
}

package com.bonju.review.repository;

import com.bonju.review.entity.Quiz;

import java.util.List;

public interface QuizRepository {

    List<Quiz> findQuizzesCreatedWithin30DaysByUser(Long userId);
}

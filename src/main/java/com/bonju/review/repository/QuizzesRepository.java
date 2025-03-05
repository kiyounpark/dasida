package com.bonju.review.repository;

import com.bonju.review.entity.Quiz;
import com.bonju.review.entity.User;

import java.util.List;

public interface QuizzesRepository {
    List<Quiz> findQuizzesByDaysAgo(User user, int days);
}

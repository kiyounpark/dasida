package com.bonju.review.quiz.repository.quizzes;

import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.user.entity.User;

import java.util.List;

public interface QuizzesRepository {
    List<Quiz> findUnsolvedOrAlwaysWrongQuizzes(User user, int days);
}

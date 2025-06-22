package com.bonju.review.quiz.repository.find;

import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.user.entity.User;

public interface QuizFindRepository {
    Quiz findById(Long id);

    boolean existsQuizByUser(User user);
}

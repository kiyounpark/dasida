package com.bonju.review.quiz.service.find;

import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.user.entity.User;

public interface QuizFindService {

    Quiz findQuizByIdAndUser(Long quizId, User user);

    boolean hasQuizByUser();

}

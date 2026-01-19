package com.bonju.review.quiz.repository.quizzes;

import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.user.entity.User;

import java.util.List;

public interface QuizzesRepository {
    List<Quiz> findUnsolvedOrAlwaysWrongQuizzes(User user, int days);

    /**
     * 유튜브 시연용: 사용자의 모든 미풀이/오답 퀴즈를 날짜 제한 없이 조회
     */
    List<Quiz> findAllUnsolvedOrAlwaysWrongQuizzes(User user);
}

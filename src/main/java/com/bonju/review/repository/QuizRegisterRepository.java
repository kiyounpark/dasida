package com.bonju.review.repository;

import com.bonju.review.entity.Quiz;

import java.util.List;

public interface QuizRegisterRepository {

    void registerQuiz(List<Quiz> quizzes);
}

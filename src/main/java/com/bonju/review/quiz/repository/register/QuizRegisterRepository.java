package com.bonju.review.quiz.repository.register;

import com.bonju.review.quiz.entity.Quiz;

import java.util.List;

public interface QuizRegisterRepository {

    void registerQuiz(List<Quiz> quizzes);
}

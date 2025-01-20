package com.bonju.review.repository;

import com.bonju.review.entity.Knowledge;
import com.bonju.review.entity.Quiz;

import java.util.List;

public interface OpenAiRepository {

    void saveQuizzes(List<Quiz> quizzes);

    void saveKnowledge(Knowledge knowledge);
}

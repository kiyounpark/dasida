package com.bonju.review.quiz.repository.register;

import com.bonju.review.quiz.entity.Quiz;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QuizRegisterRepositoryJpa implements QuizRegisterRepository {

    private final EntityManager em;

    @Override
    public void registerQuiz(List<Quiz> quizzes) {
        quizzes.forEach(em::persist);
    }

}

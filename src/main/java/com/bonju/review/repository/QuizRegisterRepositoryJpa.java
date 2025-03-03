package com.bonju.review.repository;

import com.bonju.review.entity.Quiz;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

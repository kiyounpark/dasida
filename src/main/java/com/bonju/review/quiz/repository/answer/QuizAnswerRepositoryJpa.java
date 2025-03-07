package com.bonju.review.quiz.repository.answer;

import com.bonju.review.quiz.entity.UserAnswer;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QuizAnswerRepositoryJpa implements QuizAnswerRepository {

    private final EntityManager em;

    @Override
    public void save(UserAnswer userAnswer) {
        em.persist(userAnswer);
    }
}
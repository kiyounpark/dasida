package com.bonju.review.quiz.repository.find;

import com.bonju.review.quiz.entity.Quiz;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QuizFindRepositoryImpl implements QuizFindRepository {

    private final EntityManager em;
    @Override
    public Quiz findById(Long id) {
        return em.find(Quiz.class, id);
    }
}

package com.bonju.review.repository;

import com.bonju.review.entity.Quiz;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
@RequiredArgsConstructor
public class QuizFindRepositoryImpl implements QuizFindRepository {

    private final EntityManager em;
    @Override
    public Quiz findById(Long id) {
        return em.find(Quiz.class, id);
    }
}

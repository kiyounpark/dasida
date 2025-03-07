package com.bonju.review.repository;

import com.bonju.review.entity.UserAnswer;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserAnswerRepositoryJpa implements UserAnswerRepository {

    private final EntityManager em;

    @Override
    public void save(UserAnswer userAnswer) {
        em.persist(userAnswer);
    }
}
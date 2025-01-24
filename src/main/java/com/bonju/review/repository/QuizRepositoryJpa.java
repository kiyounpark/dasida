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
public class QuizRepositoryJpa implements QuizRepository {

    private final EntityManager em;

    public List<Quiz> findQuizzesCreatedWithin30DaysByUser(Long userId) {
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(30);
        String jpql = "SELECT q " +
                "FROM Quiz q " +
                "WHERE q.createdAt >= :thresholdDate AND q.user.id = :userId " +
                "ORDER BY q.createdAt DESC";

        TypedQuery<Quiz> query = em.createQuery(jpql, Quiz.class);
        query.setParameter("thresholdDate", thresholdDate);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
}

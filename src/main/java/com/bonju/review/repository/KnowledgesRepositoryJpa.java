package com.bonju.review.repository;

import com.bonju.review.entity.Knowledge;
import com.bonju.review.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class KnowledgesRepositoryJpa implements KnowledgesRepository {

    private final EntityManager em;

    @Override
    public List<Knowledge> findKnowledgesByDaysAgo(User user, int days) {
        LocalDateTime start = LocalDate.now().minusDays(days).atStartOfDay();
        LocalDateTime end = LocalDate.now().minusDays(days - 1).atStartOfDay();

        return em.createQuery(
                        "SELECT k FROM Knowledge k WHERE k.user = :user AND k.createdAt >= :start AND k.createdAt < :end",
                        Knowledge.class)
                .setParameter("user", user)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }
}

package com.bonju.review.knowledge.repository.knowledges;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.user.entity.User;
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
        LocalDateTime end = start.plusDays(1);

        return em.createQuery(
                        "SELECT k FROM Knowledge k " +
                                "WHERE k.user = :user " +
                                "AND k.createdAt >= :start " +
                                "AND k.createdAt < :end " +
                                "ORDER BY k.createdAt ASC",  // ORDER BY 절 추가
                        Knowledge.class)
                .setParameter("user", user)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }
}

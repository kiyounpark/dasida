package com.bonju.review.knowledge.repository.knowledge_list;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.vo.SingleDayRange;
import com.bonju.review.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TodayKnowledgeListRepositoryJpa implements TodayKnowledgeListRepository {

    private final EntityManager em;

    @Override
    public List<Knowledge> findKnowledgeListByDateRange(User user, SingleDayRange dayRange) {
        return em.createQuery(
                        "SELECT k FROM Knowledge k " +
                                "WHERE k.user = :user " +
                                "  AND k.createdAt >= :start " +
                                "  AND k.createdAt < :end " ,
                        Knowledge.class)
                .setParameter("user", user)
                .setParameter("start", dayRange.getStart())
                .setParameter("end", dayRange.getEnd())
                .getResultList();
    }
}

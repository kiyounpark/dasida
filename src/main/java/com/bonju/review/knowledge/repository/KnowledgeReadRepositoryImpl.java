package com.bonju.review.knowledge.repository;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class KnowledgeReadRepositoryImpl implements KnowledgeReadRepository {

  private final EntityManager em;

  @Override
  public Optional<Knowledge> findKnowledge(User user, Long id) {
    String jpql = "SELECT k FROM Knowledge k WHERE k.id = :id AND k.user = :user";
    return em.createQuery(jpql, Knowledge.class)
            .setParameter("id", id)
            .setParameter("user", user)
            .getResultStream()
            .findFirst();
  }

  @Override
  public boolean hasRegisteredKnowledge(User user) {
    List<Long> foundIdList = em.createQuery(
                    "select k.id from Knowledge k where k.user = :user",
                    Long.class)
            .setParameter("user", user)
            .setMaxResults(1) // 첫 행만 확인하여 조기 종료
            .getResultList();

    int resultCount = foundIdList.size();
    return resultCount > 0;
  }
}

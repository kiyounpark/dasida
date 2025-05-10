package com.bonju.review.knowledge.repository;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}

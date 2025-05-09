package com.bonju.review.knowledge.repository;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class KnowledgeListRepositoryJpa implements KnowledgeListRepository {

  private final EntityManager em;
  @Override
  public List<Knowledge> findKnowledgeList(User user, int offset, int limit) {
      return em.createQuery(
              "SELECT k FROM Knowledge k JOIN FETCH k.user WHERE k.user = :user ORDER BY k.createdAt DESC",
              Knowledge.class
          )
          .setParameter("user", user)
          .setFirstResult(offset)
          .setMaxResults(limit)
          .getResultList();
  }
}

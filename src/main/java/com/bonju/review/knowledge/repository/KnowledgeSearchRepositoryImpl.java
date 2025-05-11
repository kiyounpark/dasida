package com.bonju.review.knowledge.repository;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class KnowledgeSearchRepositoryImpl implements KnowledgeSearchRepository{

  private final EntityManager em;

  @Override
  public List<Knowledge> findByTitleContaining(User user, String title) {
    if (user == null || title == null || title.trim().isBlank()) {
      return List.of();
    }

    return em.createQuery(
                    """
                    SELECT k
                    FROM Knowledge k
                    WHERE k.user = :user
                      AND k.title LIKE CONCAT('%', :title, '%')
                    ORDER BY k.createdAt DESC
                    """,
                    Knowledge.class)
            .setParameter("user", user)
            .setParameter("title", title.trim())
            .getResultList();
  }
}

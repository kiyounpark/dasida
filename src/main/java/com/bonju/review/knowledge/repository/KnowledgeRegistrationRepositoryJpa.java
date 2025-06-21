package com.bonju.review.knowledge.repository;

import com.bonju.review.knowledge.entity.Knowledge;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class KnowledgeRegistrationRepositoryJpa implements KnowledgeRegistrationRepository {

  private final EntityManager em;
  @Override
  public Knowledge save(Knowledge knowledge) {
    em.persist(knowledge);
    return knowledge;
  }
}

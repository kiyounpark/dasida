package com.bonju.review.useranswer.repository;

import com.bonju.review.useranswer.entity.UserAnswer;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UserAnswerRepositoryJpa implements UserAnswerRepository {

  private final EntityManager em;
  @Override
  public void save(UserAnswer userAnswer) {
    em.persist(userAnswer);
  }
}

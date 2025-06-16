package com.bonju.review.useranswer.repository;

import com.bonju.review.user.entity.User;
import com.bonju.review.useranswer.entity.UserAnswer;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class UserAnswerRepositoryJpa implements UserAnswerRepository {

  private final EntityManager em;
  @Override
  public void save(UserAnswer userAnswer) {
    em.persist(userAnswer);
  }

  public List<UserAnswer> findAll(User user) {
    return em.createQuery(
                    "SELECT ua FROM UserAnswer ua WHERE ua.user = :user", UserAnswer.class)
            .setParameter("user", user)
            .getResultList();
  }
}

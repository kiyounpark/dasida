package com.bonju.review.quiz.repository.find;

import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QuizFindRepositoryJPA implements QuizFindRepository {

    private final EntityManager em;
    @Override
    public Quiz findById(Long id) {
        return em.find(Quiz.class, id);
    }

    @Override
    public boolean isQuizListEmptyByUser(User user) {
        String jpql = """
                SELECT 1
                  FROM Quiz q
                 WHERE q.user  = :user
                """;

        List<Integer> result = em.createQuery(
                        jpql,
                        Integer.class)
                .setParameter("user",  user)
                .setMaxResults(1)
                .getResultList();

        return result.isEmpty();
    }
}

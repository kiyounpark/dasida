package com.bonju.review.repository;

import com.bonju.review.entity.Knowledge;
import com.bonju.review.entity.Quiz;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class OpenAiRepositoryJpa implements OpenAiRepository {

    private final EntityManager em;

    @Override
    public void saveQuizzes(List<Quiz> quizzes) {
        quizzes.forEach(em::persist);
    }

    @Override
    public void saveKnowledge(Knowledge knowledge) {
        em.persist(knowledge);
    }
}


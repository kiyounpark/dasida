package com.bonju.review.repository;


import com.bonju.review.entity.Knowledge;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class KnowledgeRepositoryImpl implements KnowledgeRepository {

    private final EntityManager em;

    @Override
    public void registerKnowledge(Knowledge knowledge) {
        em.persist(knowledge);
    }
}

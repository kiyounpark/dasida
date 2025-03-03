package com.bonju.review.repository;


import com.bonju.review.entity.Knowledge;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Lob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class KnowledgeRepositoryImpl implements KnowledgeRepository {

    private final EntityManager em;

    @Override
    public Long registerKnowledge(Knowledge knowledge) {
        em.persist(knowledge);
        return knowledge.getId();
    }
}

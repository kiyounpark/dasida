package com.bonju.review.knowledge.repository.register;


import com.bonju.review.knowledge.entity.Knowledge;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class KnowledgeRegisterRepositoryImpl implements KnowledgeRegisterRepository {

    private final EntityManager em;

    @Override
    public Knowledge registerKnowledge(Knowledge knowledge) {
        em.persist(knowledge);
        return knowledge;
    }
}

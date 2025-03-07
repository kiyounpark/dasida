package com.bonju.review.knowledge.repository.register;

import com.bonju.review.knowledge.entity.Knowledge;

public interface KnowledgeRepository {

    Knowledge registerKnowledge(Knowledge knowledge);
}

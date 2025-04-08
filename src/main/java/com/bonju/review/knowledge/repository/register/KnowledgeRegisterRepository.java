package com.bonju.review.knowledge.repository.register;

import com.bonju.review.knowledge.entity.Knowledge;

public interface KnowledgeRegisterRepository {

    Knowledge registerKnowledge(Knowledge knowledge);
}

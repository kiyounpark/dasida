package com.bonju.review.knowledge.service.register;

import com.bonju.review.knowledge.dto.KnowledgeRegisterRequestDto;
import com.bonju.review.knowledge.entity.Knowledge;

public interface KnowledgeRegisterService {

    Knowledge registerKnowledge(KnowledgeRegisterRequestDto knowledgeRegisterRequestDto);
}

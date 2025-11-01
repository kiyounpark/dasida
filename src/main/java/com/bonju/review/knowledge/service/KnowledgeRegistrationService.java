package com.bonju.review.knowledge.service;

import com.bonju.review.knowledge.dto.KnowledgeRegisterRequestDto;
import com.bonju.review.knowledge.entity.Knowledge;

public interface KnowledgeRegistrationService {
  Knowledge registerKnowledge(KnowledgeRegisterRequestDto knowledgeRegisterRequestDto);
}

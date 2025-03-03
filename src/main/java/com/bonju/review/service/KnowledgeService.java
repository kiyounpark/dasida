package com.bonju.review.service;

import com.bonju.review.dto.KnowledgeRequestDto;
import com.bonju.review.entity.Knowledge;

public interface KnowledgeService {

    Knowledge registerKnowledge(KnowledgeRequestDto knowledgeRequestDto);
}

package com.bonju.review.knowledge.service;

import com.bonju.review.knowledge.dto.KnowledgeDetailResponseDto;

public interface KnowledgeReadService {

  KnowledgeDetailResponseDto getKnowledgeById(Long id);
}

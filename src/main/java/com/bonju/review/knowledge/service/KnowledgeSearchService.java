package com.bonju.review.knowledge.service;

import com.bonju.review.knowledge.dto.KnowledgeSearchResponseDto;

public interface KnowledgeSearchService {

  KnowledgeSearchResponseDto searchKnowledgeByTitle(String title);
}

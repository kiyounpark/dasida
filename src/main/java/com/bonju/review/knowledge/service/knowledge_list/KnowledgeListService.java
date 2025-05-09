package com.bonju.review.knowledge.service.knowledge_list;

import com.bonju.review.knowledge.dto.KnowledgeListResponseDto;

public interface KnowledgeListService {
  KnowledgeListResponseDto getKnowledgeList(int offset);
}

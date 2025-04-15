package com.bonju.review.knowledge.service.knowledge_list;

import com.bonju.review.knowledge.dto.DayKnowledgeResponseDto;
import com.google.common.collect.ImmutableList;

public interface KnowledgeListService {
    ImmutableList<DayKnowledgeResponseDto> getAllDayKnowledges();
}

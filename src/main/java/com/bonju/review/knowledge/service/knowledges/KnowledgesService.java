package com.bonju.review.knowledge.service.knowledges;

import com.bonju.review.knowledge.dto.DayKnowledgeResponseDto;
import com.google.common.collect.ImmutableList;

public interface KnowledgesService {
    ImmutableList<DayKnowledgeResponseDto> getAllDayKnowledges();
}

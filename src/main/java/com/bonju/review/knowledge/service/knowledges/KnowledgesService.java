package com.bonju.review.knowledge.service.knowledges;

import com.bonju.review.knowledge.dto.DayKnowledgeResponseDto;

import java.util.List;

public interface KnowledgesService {
    List<DayKnowledgeResponseDto> getAllDayKnowledges();
}

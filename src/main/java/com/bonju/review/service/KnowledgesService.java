package com.bonju.review.service;

import com.bonju.review.dto.DayKnowledgeResponseDto;

import java.util.List;

public interface KnowledgesService {
    List<DayKnowledgeResponseDto> getAllDayKnowledges();
}

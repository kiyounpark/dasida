package com.bonju.review.knowledge.service.knowledge_list;

import com.bonju.review.knowledge.dto.DayKnowledgeResponseDto;
import com.google.common.collect.ImmutableList;

public interface TodayKnowledgeListService {
    ImmutableList<DayKnowledgeResponseDto> getAllDayKnowledges();

    /**
     * 유튜브 시연용: 날짜 필터링 없이 모든 지식 조회
     */
    ImmutableList<DayKnowledgeResponseDto> getAllKnowledgesForDemo();
}

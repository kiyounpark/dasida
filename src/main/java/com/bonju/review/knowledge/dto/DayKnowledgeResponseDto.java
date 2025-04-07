package com.bonju.review.knowledge.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DayKnowledgeResponseDto {

    private final int dayType; // "0일차", "3일차", "7일차", "30일차"
    private final Long id;
    private final String title;
    private final String content;

    @Builder
    private DayKnowledgeResponseDto(int dayType, Long id, String title, String content) {
        this.dayType = dayType;
        this.id = id;
        this.title = title;
        this.content = content;
    }
}

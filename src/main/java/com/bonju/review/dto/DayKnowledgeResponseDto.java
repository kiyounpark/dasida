package com.bonju.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DayKnowledgeResponseDto {
    private final int dayType; // "0일차", "3일차", "7일차", "30일차"
    private final Long id;
    private final String title;
    private final String content;
}

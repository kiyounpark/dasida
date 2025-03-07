package com.bonju.review.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class HomeResponseDto {
    private final List<DayQuizResponseDto> quizzes;
    private final List<DayKnowledgeResponseDto> knowledges;
}
package com.bonju.review.home.dto;

import com.bonju.review.knowledge.dto.DayKnowledgeResponseDto;
import com.bonju.review.quiz.dto.DayQuizResponseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class HomeResponseDto {
    private final List<DayQuizResponseDto> quizzes;
    private final List<DayKnowledgeResponseDto> knowledges;
}
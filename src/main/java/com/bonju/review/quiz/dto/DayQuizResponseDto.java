package com.bonju.review.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
@Getter
@AllArgsConstructor
@Builder
public class DayQuizResponseDto {
    private final int dayType;  // 내부적으로 Enum 보관
    private final Long quizId;
    private final String question;
    private String hint;
    private final int answerLength;

}
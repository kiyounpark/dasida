package com.bonju.review.dto;

import com.bonju.review.enums.DayType;
import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public class DayQuizResponseDto {
    private final int dayType;  // 내부적으로 Enum 보관
    private final Long quizId;
    private final String question;
}
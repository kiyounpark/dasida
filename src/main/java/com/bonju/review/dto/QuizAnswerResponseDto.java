package com.bonju.review.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class QuizAnswerResponseDto {
    private Long userAnswerId;
    private boolean correct;
}

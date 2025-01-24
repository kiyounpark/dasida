package com.bonju.review.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class QuizResponseDto {
    private final Long id;
    private final String quiz;
}

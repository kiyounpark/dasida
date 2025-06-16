package com.bonju.review.wronganswernote.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WrongAnswerResponseDto {
    // UserAnswer 엔티티의 식별자
    private Long userAnswerId;

    private boolean isCorrect;

    // 사용자가 입력한 답변
    private String userAnswer;

    // 0, 3, 7, 30 등 복습 주기
    private int dayType;
}

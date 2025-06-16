package com.bonju.review.wronganswernote.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class WrongAnswerGroupResponseDto {

    private Long quizId;
    private String quizText;         // 퀴즈 질문(문제)
    private String quizAnswer;    // 퀴즈 정답
    private List<WrongAnswerResponseDto> answers; // 사용자의 여러 오답(답변) 목록
}

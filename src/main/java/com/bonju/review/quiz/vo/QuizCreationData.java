package com.bonju.review.quiz.vo;

import lombok.Builder;

@Builder
public record QuizCreationData(String question, String answer, String hint) {
}

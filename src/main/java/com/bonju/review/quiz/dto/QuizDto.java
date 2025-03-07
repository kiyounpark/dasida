package com.bonju.review.quiz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class QuizDto {
    @JsonProperty("quiz") // json 키값 지정
    private final String quiz;

    @JsonProperty("answer") // json 키값 지정
    private final String answer;

    @JsonProperty("hint") // json 키값 지정
    private final String hint;
}

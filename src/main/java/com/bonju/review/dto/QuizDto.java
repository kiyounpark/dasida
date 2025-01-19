package com.bonju.review.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class QuizDto {
    @JsonProperty("quiz")
    private final String quiz;

    @JsonProperty("answer")
    private final String answer;

    @JsonProperty("commentary")
    private final String commentary;
}

package com.bonju.review.quiz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter // Lombok을 사용하여 Getter 자동 생성
public final class QuizWrapperDto {
    @JsonProperty("quizzes") // json 키 값 지장
    private final List<QuizDto> quizzes;

    // 생성자를 통해 필드 초기화
    public QuizWrapperDto(@JsonProperty("quizzes") List<QuizDto> quizzes) {
        this.quizzes = Collections.unmodifiableList(quizzes);
    }

}

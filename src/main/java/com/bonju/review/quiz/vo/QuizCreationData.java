package com.bonju.review.quiz.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class QuizCreationData {
  private final String question;
  private final String answer;
  private final String hint;
}

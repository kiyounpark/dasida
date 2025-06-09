package com.bonju.review.useranswer.command;


public record SubmitUserAnswerCommand(
        Long   quizId,
        String answer,
        Integer    dayType) {
  }
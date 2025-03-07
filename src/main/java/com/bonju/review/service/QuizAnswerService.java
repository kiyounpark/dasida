package com.bonju.review.service;

import com.bonju.review.dto.QuizAnswerRequestDto;
import com.bonju.review.dto.QuizAnswerResponseDto;

public interface QuizAnswerService {

    QuizAnswerResponseDto submitAnswer(QuizAnswerRequestDto answerDto);
}

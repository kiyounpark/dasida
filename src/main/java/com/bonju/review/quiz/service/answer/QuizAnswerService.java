package com.bonju.review.quiz.service.answer;

import com.bonju.review.quiz.dto.QuizAnswerRequestDto;
import com.bonju.review.quiz.dto.QuizAnswerResponseDto;

public interface QuizAnswerService {

    QuizAnswerResponseDto submitAnswer(Long quizId, QuizAnswerRequestDto answerDto);
}

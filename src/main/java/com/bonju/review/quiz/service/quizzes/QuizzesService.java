package com.bonju.review.quiz.service.quizzes;

import com.bonju.review.quiz.dto.DayQuizResponseDto;

import java.util.List;

public interface QuizzesService {
    List<DayQuizResponseDto> getAllDayQuizzes();
}

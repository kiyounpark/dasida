package com.bonju.review.service;

import com.bonju.review.dto.DayQuizResponseDto;

import java.util.List;

public interface QuizzesService {
    List<DayQuizResponseDto> getAllDayQuizzes();
}

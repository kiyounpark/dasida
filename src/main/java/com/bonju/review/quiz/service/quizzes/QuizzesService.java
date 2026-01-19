package com.bonju.review.quiz.service.quizzes;

import com.bonju.review.quiz.dto.DayQuizResponseDto;

import java.util.List;

public interface QuizzesService {
    List<DayQuizResponseDto> getAllDayQuizzes();

    /**
     * 유튜브 시연용: 날짜 필터링 없이 모든 퀴즈 조회
     */
    List<DayQuizResponseDto> getAllQuizzesForDemo();
}

package com.bonju.review.service;


import com.bonju.review.dto.DayQuizResponseDto;
import com.bonju.review.entity.Quiz;
import com.bonju.review.entity.User;
import com.bonju.review.enums.DayType;
import com.bonju.review.repository.QuizzesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizzesServiceImpl implements QuizzesService{
    private final QuizzesRepository quizzesRepository;
    private final UserService userService;

    /**
     * 0일차, 3일차, 7일차, 30일차에 해당하는 퀴즈를 모두 조회하여 하나의 DTO 리스트로 반환합니다.
     * 특정 일차에 해당하는 데이터가 없으면, 해당 일차는 결과에 포함되지 않습니다.
     */
    public List<DayQuizResponseDto> getAllDayQuizzes() {
        User user = userService.findUser();
        List<DayQuizResponseDto> result = new ArrayList<>();

        // DayType Enum을 순회 (ZERO, THREE, SEVEN, THIRTY)
        for (DayType dayType : DayType.values()) {
            int daysAgo = dayType.getDaysAgo();  // 예: 0, 3, 7, 30
            // Repository를 통해 해당 날짜 범위의 퀴즈 조회
            List<Quiz> quizzes = quizzesRepository.findQuizzesByDaysAgo(user, daysAgo);
            // 조회된 퀴즈를 DTO로 변환하여 결과 리스트에 추가
            for (Quiz quiz : quizzes) {
                result.add(new DayQuizResponseDto(dayType.getDaysAgo(), quiz.getId(), quiz.getQuiz()));
            }
        }

        return result;
    }
}



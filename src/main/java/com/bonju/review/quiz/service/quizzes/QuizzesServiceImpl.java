package com.bonju.review.quiz.service.quizzes;


import com.bonju.review.quiz.dto.DayQuizResponseDto;
import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.quiz.exception.errorcode.QuizErrorCode;
import com.bonju.review.quiz.exception.exception.QuizException;
import com.bonju.review.user.entity.User;
import com.bonju.review.util.enums.DayType;
import com.bonju.review.quiz.repository.quizzes.QuizzesRepository;
import com.bonju.review.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizzesServiceImpl implements QuizzesService {

    private final QuizzesRepository quizzesRepository;
    private final UserService userService;

    /**
     * 0·3·7·30일차 “미풀이 or 오답” 퀴즈를 조회해 DTO로 반환한다.
     * 0번째 요소(※0일차)만 고정, 나머지는 무작위 순서로 섞는다.
     */
    @Override
    @Transactional(readOnly = true)
    public List<DayQuizResponseDto> getAllDayQuizzes() {

        User user = userService.findUser();
        List<DayQuizResponseDto> result = new ArrayList<>();

        for (DayType dayType : DayType.values()) {

            List<Quiz> quizzes;

            try {
                quizzes = quizzesRepository.findUnsolvedOrAlwaysWrongQuizzes(
                        user, dayType.getDaysAgo());
            } catch (DataAccessException e) {
                throw new QuizException(QuizErrorCode.QUIZ_TODAY_FAILED, e);
            }

            for (Quiz quiz : quizzes) {
                result.add(DayQuizResponseDto.builder()
                        .dayType(dayType.getDaysAgo())
                        .quizId(quiz.getId())
                        .question(quiz.getQuestion())
                        .answerLength(quiz.getAnswerLength())
                        .hint(quiz.getHint())
                        .build());
            }
        }

        // ❸ 0번째는 고정, 이후 구간만 셔플
        if (result.size() > 2) {                      // 섞을 대상이 2개 이상일 때만
            Collections.shuffle(result.subList(1, result.size()));
        }

        return result;
    }
}



package com.bonju.review.quiz.mapper;

import com.bonju.review.quiz.dto.QuizDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuizEntityMapper {

    public List<Quiz> convertToEntities(User user, List<QuizDto> quizDtos, Knowledge knowledge) {
        return quizDtos.stream()
                .map(dto -> new Quiz(
                        user,
                        knowledge,
                        dto.getQuiz(),
                        dto.getAnswer(),
                        dto.getHint()
                ))
                .collect(Collectors.toList()); // 결과를 리스트로 수집
    }
}


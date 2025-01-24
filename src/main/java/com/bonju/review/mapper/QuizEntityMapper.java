package com.bonju.review.mapper;

import com.bonju.review.dto.QuizDto;
import com.bonju.review.entity.Knowledge;
import com.bonju.review.entity.Quiz;
import com.bonju.review.entity.User;
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
                        dto.getCommentary()
                ))
                .collect(Collectors.toList()); // 결과를 리스트로 수집
    }
}


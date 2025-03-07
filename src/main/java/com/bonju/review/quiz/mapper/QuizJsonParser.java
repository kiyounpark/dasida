package com.bonju.review.quiz.mapper;

import com.bonju.review.quiz.dto.QuizDto;
import com.bonju.review.quiz.dto.QuizWrapperDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j // SLF4J 로거 활성화
public class QuizJsonParser {
    private final ObjectMapper objectMapper;

    public QuizJsonParser() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    public List<QuizDto> parse(String jsonQuizData) {
        log.info("JSON 데이터 파싱 시도: {}", jsonQuizData); // JSON 파싱 시작 로깅
        try {
            QuizWrapperDto quizWrapper = objectMapper.readValue(jsonQuizData, QuizWrapperDto.class);
            log.info("JSON 데이터를 QuizWrapperDto로 성공적으로 변환하였습니다. 퀴즈 개수: {}개",
                    quizWrapper.getQuizzes().size()); // 파싱 성공 로깅
            return quizWrapper.getQuizzes();
        } catch (Exception e) {
            log.error("JSON 데이터를 QuizWrapperDto로 변환하는 데 실패하였습니다. 원본 JSON 데이터: {}", jsonQuizData, e); // 에러 로깅
            throw new RuntimeException("JSON 데이터를 변환하는 과정에서 실패하였습니다.", e);
        }
    }
}

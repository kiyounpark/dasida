package com.bonju.review.service;

import com.bonju.review.client.OpenAiClient;
import com.bonju.review.dto.KnowledgeDto;
import com.bonju.review.dto.QuizDto;
import com.bonju.review.entity.Knowledge;
import com.bonju.review.entity.Quiz;
import com.bonju.review.mapper.KnowledgeMapper;
import com.bonju.review.mapper.QuizEntityMapper;
import com.bonju.review.mapper.QuizJsonParser;
import com.bonju.review.repository.OpenAiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final OpenAiClient openAiClient;
    private final OpenAiRepository openAiRepository;
    private final QuizJsonParser quizJsonParser;
    private final QuizEntityMapper quizEntityMapper;
    private final KnowledgeMapper knowledgeMapper;

    @Transactional
    public void saveKnowledge(Knowledge knowledge) {
        openAiRepository.saveKnowledge(knowledge); // 지식 저장
    }

    @Transactional
    public void saveQuizzes(String jsonQuizData, Knowledge knowledge) {
        List<QuizDto> quizDtos = quizJsonParser.parse(jsonQuizData);
        List<Quiz> quizzes = quizEntityMapper.convertToEntities(quizDtos, knowledge);
        openAiRepository.saveQuizzes(quizzes); // 퀴즈 저장
    }

    @Transactional
    public void saveQuiz(KnowledgeDto knowledgeDto) {
        // 객체 변환을 한 번만 수행
        Knowledge knowledge = knowledgeMapper.toEntity(knowledgeDto);

        // 지식 저장
        saveKnowledge(knowledge);

        // 퀴즈 저장
        String prompt = knowledgeDto.getDescription();
        String jsonQuizData = openAiClient.getQuizJson(prompt);
        saveQuizzes(jsonQuizData, knowledge);
    }
}

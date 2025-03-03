package com.bonju.review.service;

import com.bonju.review.client.OpenAiClient;
import com.bonju.review.dto.KnowledgeRequestDto;
import com.bonju.review.dto.QuizDto;
import com.bonju.review.entity.Knowledge;
import com.bonju.review.entity.Quiz;
import com.bonju.review.entity.User;
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
    private final UserService userService;

    @Transactional
    public void saveQuiz(KnowledgeRequestDto knowledgeRequestDto) {
        User user = userService.findUser();

        Knowledge knowledge = knowledgeMapper.toEntity(user, knowledgeRequestDto);
        saveKnowledge(knowledge);

        String jsonQuizData = openAiClient.getQuizJson(knowledgeRequestDto.getContentHtml());
        saveQuizzes(user, jsonQuizData, knowledge);
    }

    private void saveKnowledge(Knowledge knowledge) {
        openAiRepository.saveKnowledge(knowledge);
    }

    private void saveQuizzes(User user, String jsonQuizData, Knowledge knowledge) {
        List<QuizDto> quizDtos = quizJsonParser.parse(jsonQuizData);
        List<Quiz> quizzes = quizEntityMapper.convertToEntities(user, quizDtos, knowledge);
        openAiRepository.saveQuizzes(quizzes);
    }


}


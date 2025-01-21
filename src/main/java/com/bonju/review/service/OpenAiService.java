package com.bonju.review.service;

import com.bonju.review.AuthenticationHelper;
import com.bonju.review.client.OpenAiClient;
import com.bonju.review.dto.KnowledgeDto;
import com.bonju.review.dto.QuizDto;
import com.bonju.review.entity.Knowledge;
import com.bonju.review.entity.Quiz;
import com.bonju.review.entity.User;
import com.bonju.review.mapper.KnowledgeMapper;
import com.bonju.review.mapper.QuizEntityMapper;
import com.bonju.review.mapper.QuizJsonParser;
import com.bonju.review.repository.OpenAiRepository;
import com.bonju.review.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final OpenAiClient openAiClient;
    private final OpenAiRepository openAiRepository;
    private final QuizJsonParser quizJsonParser;
    private final QuizEntityMapper quizEntityMapper;
    private final KnowledgeMapper knowledgeMapper;
    private final UserRepository userRepository;

    @Transactional
    public void saveQuiz(KnowledgeDto knowledgeDto) {
        String kakaoId = AuthenticationHelper.getKaKaoId();
        User user = findUserByKaKaoId(kakaoId);

        Knowledge knowledge = knowledgeMapper.toEntity(user, knowledgeDto);
        saveKnowledge(knowledge);

        String jsonQuizData = openAiClient.getQuizJson(knowledgeDto.getDescription());
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

    private User findUserByKaKaoId(String kakaoId) {
        return userRepository.findByKaKaoId(kakaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));
    }
}


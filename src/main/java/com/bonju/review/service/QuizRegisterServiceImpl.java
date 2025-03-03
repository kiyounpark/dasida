package com.bonju.review.service;

import com.bonju.review.client.AiClient;
import com.bonju.review.dto.QuizDto;
import com.bonju.review.entity.Knowledge;
import com.bonju.review.entity.Quiz;
import com.bonju.review.entity.User;
import com.bonju.review.extractor.ImageExtractor;
import com.bonju.review.mapper.QuizEntityMapper;
import com.bonju.review.mapper.QuizJsonParser;
import com.bonju.review.repository.QuizRegisterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class QuizRegisterServiceImpl implements QuizRegisterService {

    private final AiClient aiClient;
    private final ImageExtractor imageExtractor;
    private final QuizJsonParser quizJsonParser;
    private final QuizEntityMapper quizEntityMapper;
    private final QuizRegisterRepository quizRegisterRepository;
    private final UserService userService;

    @Override
    public void createQuiz(Knowledge knowledge) {
        User user = userService.findUser();

        List<String> extractImageSrc = imageExtractor.extractImageSrc(knowledge.getContentHtml());
        String quizJson = aiClient.getQuizJson(knowledge.getContentHtml(), extractImageSrc);
        List<QuizDto> quizDtos = quizJsonParser.parse(quizJson);
        List<Quiz> quizzes = quizEntityMapper.convertToEntities(user, quizDtos, knowledge);
        registerQuiz(quizzes);
    }

    @Transactional
    private void registerQuiz(List<Quiz> quizzes){
        quizRegisterRepository.registerQuiz(quizzes);
    }
}

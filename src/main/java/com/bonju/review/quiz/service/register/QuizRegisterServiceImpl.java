package com.bonju.review.quiz.service.register;

import com.bonju.review.quiz.client.AiClient;
import com.bonju.review.quiz.dto.QuizDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.user.entity.User;
import com.bonju.review.knowledge.extractor.ImageExtractor;
import com.bonju.review.quiz.mapper.QuizEntityMapper;
import com.bonju.review.quiz.mapper.QuizJsonParser;
import com.bonju.review.quiz.repository.register.QuizRegisterRepository;
import com.bonju.review.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    @Transactional
    public void createQuiz(Knowledge knowledge) {
        User user = userService.findUser();

        List<String> extractImageSrc = imageExtractor.extractImageSrc(knowledge.getContent());
        String quizJson = aiClient.getQuizJson(knowledge.getContent(), extractImageSrc);
        List<QuizDto> quizDtos = quizJsonParser.parse(quizJson);
        List<Quiz> quizzes = quizEntityMapper.convertToEntities(user, quizDtos, knowledge);
        registerQuiz(quizzes);
    }

    private void registerQuiz(List<Quiz> quizzes){
        quizRegisterRepository.registerQuiz(quizzes);
    }
}

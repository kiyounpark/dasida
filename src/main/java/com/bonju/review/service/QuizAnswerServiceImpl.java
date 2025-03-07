package com.bonju.review.service;

import com.bonju.review.dto.QuizAnswerRequestDto;
import com.bonju.review.dto.QuizAnswerResponseDto;
import com.bonju.review.entity.Quiz;
import com.bonju.review.entity.User;
import com.bonju.review.entity.UserAnswer;
import com.bonju.review.repository.UserAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizAnswerServiceImpl implements QuizAnswerService {
    private final UserService userService;
    private final QuizFindService quizFindService;
    private final UserAnswerRepository userAnswerRepositoryJpa;
    // TODO private final WrongAnswerNoteService wrongAnswerNoteService;
    @Override
    public QuizAnswerResponseDto submitAnswer(QuizAnswerRequestDto answerDto) {
        User user = userService.findUser();
        Quiz quiz = quizFindService.findQuizById(answerDto.getQuizId());

        boolean isWrong = !quiz.getAnswer().equalsIgnoreCase(answerDto.getAnswer());

        UserAnswer userAnswer = new UserAnswer(user, quiz, answerDto.getAnswer(), answerDto.getDayType(), isWrong);
        userAnswerRepositoryJpa.save(userAnswer);

        //TODO
//        if (isWrong) {
//            wrongAnswerNoteService.saveWrongAnswer(user, quiz, answerDto.getDayType());
//        }

        return new QuizAnswerResponseDto(userAnswer.getId(), !isWrong);
    }
}

package com.bonju.review.quiz.service.answer;

import com.bonju.review.quiz.dto.QuizAnswerRequestDto;
import com.bonju.review.quiz.dto.QuizAnswerResponseDto;
import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.user.entity.User;
import com.bonju.review.quiz.entity.UserAnswer;
import com.bonju.review.quiz.repository.answer.QuizAnswerRepository;
import com.bonju.review.quiz.service.find.QuizFindService;
import com.bonju.review.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizAnswerServiceImpl implements QuizAnswerService {
    private final UserService userService;
    private final QuizFindService quizFindService;
    private final QuizAnswerRepository quizAnswerRepositoryJpa;
    // TODO private final WrongAnswerNoteService wrongAnswerNoteService;
    @Override
    public QuizAnswerResponseDto submitAnswer(QuizAnswerRequestDto answerDto) {
        User user = userService.findUser();
        Quiz quiz = quizFindService.findQuizById(answerDto.getQuizId());

        boolean isWrong = !quiz.getAnswer().equalsIgnoreCase(answerDto.getAnswer());

        UserAnswer userAnswer = new UserAnswer(user, quiz, answerDto.getAnswer(), answerDto.getDayType(), isWrong);
        quizAnswerRepositoryJpa.save(userAnswer);

        //TODO
//        if (isWrong) {
//            wrongAnswerNoteService.saveWrongAnswer(user, quiz, answerDto.getDayType());
//        }

        return new QuizAnswerResponseDto(userAnswer.getId(), !isWrong);
    }
}

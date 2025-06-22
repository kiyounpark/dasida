package com.bonju.review.useranswer.service;

import com.bonju.review.useranswer.dto.UserAnswerResponseDto;
import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.useranswer.entity.UserAnswer;

import com.bonju.review.useranswer.repository.UserAnswerRepository;
import com.bonju.review.quiz.service.find.QuizFindService;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import com.bonju.review.useranswer.command.SubmitUserAnswerCommand;
import com.bonju.review.useranswer.exception.UserAnswerErrorCode;
import com.bonju.review.useranswer.exception.UserAnswerException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class UserAnswerService {

  private final QuizFindService quizFindService;
  private final UserService userService;
  private final UserAnswerRepository userAnswerRepository;

  public List<UserAnswer> findAll() {
    User user = userService.findUser();
    try {
      return userAnswerRepository.findAll(user);
    } catch (DataAccessException ex) {
      throw new UserAnswerException(UserAnswerErrorCode.FIND_ALL_FAIL, ex);
    }
  }

  public UserAnswerResponseDto submitAnswer(SubmitUserAnswerCommand command) {
    User user = userService.findUser();
    Quiz quiz = quizFindService.findQuizByIdAndUser(command.quizId(), user);
    boolean isCorrect = isAnswerCorrect(command, quiz);
    registerDBUserAnswer(command, quiz, user, isCorrect);

    return new UserAnswerResponseDto(isCorrect);
  }

  private void registerDBUserAnswer(SubmitUserAnswerCommand command, Quiz quiz, User user, boolean isCorrect) {
    UserAnswer userAnswer = UserAnswer.builder()
            .quiz(quiz)
            .user(user)
            .answer(command.answer())
            .dayType(command.dayType())
            .isCorrect(isCorrect)
            .build();

    try {
      userAnswerRepository.save(userAnswer);
    } catch (DataAccessException e) {
      throw new UserAnswerException(UserAnswerErrorCode.DB_SAVE_FAIL, e);
    }
  }

  private boolean isAnswerCorrect(SubmitUserAnswerCommand command, Quiz quiz) {
    return command.answer().equals(quiz.getAnswer());
  }

}




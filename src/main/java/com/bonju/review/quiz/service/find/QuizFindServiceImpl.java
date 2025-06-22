package com.bonju.review.quiz.service.find;

import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.quiz.exception.errorcode.QuizErrorCode;
import com.bonju.review.quiz.exception.exception.QuizException;
import com.bonju.review.quiz.repository.find.QuizFindRepository;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizFindServiceImpl implements QuizFindService {
    private final QuizFindRepository quizFindRepository;
    private final UserService userService;

    @Override
    public Quiz findQuizByIdAndUser(Long quizId, User user) {
        try{
            return quizFindRepository.findById(quizId);
        }
        catch (DataAccessException e){
            throw new QuizException(QuizErrorCode.QUIZ_FIND_FAILED, e);
        }
    }

    @Override
    public boolean hasQuizByUser() {
        User user = userService.findUser();
        try {
            return quizFindRepository.isQuizListEmptyByUser(user);
        } catch (DataAccessException e){
            throw new QuizException(QuizErrorCode.QUIZ_FIND_FAILED, e);
        }
    }
}

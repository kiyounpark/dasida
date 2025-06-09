package com.bonju.review.quiz.service.find;

import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.quiz.repository.find.QuizFindRepository;
import com.bonju.review.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizFindServiceImpl implements QuizFindService {
    private final QuizFindRepository quizFindRepository;

    @Override
    public Quiz findQuizByIdAndUser(Long quizId, User user) {
        return quizFindRepository.findById(quizId);
    }
}

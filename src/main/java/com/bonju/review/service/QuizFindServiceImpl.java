package com.bonju.review.service;

import com.bonju.review.entity.Quiz;
import com.bonju.review.repository.QuizFindRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizFindServiceImpl implements QuizFindService {
    private final QuizFindRepository quizFindRepository;

    @Override
    public Quiz findQuizById(Long quizId) {
        return quizFindRepository.findById(quizId);
    }
}

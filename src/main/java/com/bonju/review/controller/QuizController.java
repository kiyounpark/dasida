package com.bonju.review.controller;

import com.bonju.review.AuthenticationHelper;
import com.bonju.review.dto.QuizResponseDto;
import com.bonju.review.entity.Quiz;
import com.bonju.review.repository.QuizRepository;
import com.bonju.review.repository.QuizRepositoryJpa;
import com.bonju.review.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping
    public ResponseEntity<List<List<QuizResponseDto>>> getQuizzes(){
        List<List<QuizResponseDto>> quizzesCreatedWithinLast30DaysByUser = quizService.getGroupedQuizzesByKnowledge();
        return ResponseEntity.ok(quizzesCreatedWithinLast30DaysByUser);
    }
}

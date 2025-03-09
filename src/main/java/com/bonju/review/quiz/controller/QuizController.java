package com.bonju.review.quiz.controller;

import com.bonju.review.quiz.dto.QuizAnswerRequestDto;
import com.bonju.review.quiz.dto.QuizAnswerResponseDto;
import com.bonju.review.quiz.service.answer.QuizAnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizAnswerService quizAnswerService;

    @PostMapping
    public ResponseEntity<QuizAnswerResponseDto> submitAnswer(
            @Valid @RequestBody QuizAnswerRequestDto answerDto) {

        QuizAnswerResponseDto responseDto = quizAnswerService.submitAnswer(answerDto);

        return ResponseEntity.ok(responseDto);
    }
}
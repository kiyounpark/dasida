package com.bonju.review.controller;

import com.bonju.review.dto.QuizAnswerRequestDto;
import com.bonju.review.dto.QuizAnswerResponseDto;
import com.bonju.review.service.QuizAnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizAnswerService quizAnswerService;

    @PostMapping("/{quizId}/answers")
    public ResponseEntity<QuizAnswerResponseDto> submitAnswer(
            @PathVariable("quizId") Long quizId,
            @Valid @RequestBody QuizAnswerRequestDto answerDto) {

        QuizAnswerResponseDto responseDto = quizAnswerService.submitAnswer(answerDto);

        return ResponseEntity.ok(responseDto);
    }
}
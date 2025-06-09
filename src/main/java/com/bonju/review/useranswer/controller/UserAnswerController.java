package com.bonju.review.useranswer.controller;

import com.bonju.review.useranswer.command.SubmitUserAnswerCommand;
import com.bonju.review.useranswer.dto.UserAnswerRequestDto;
import com.bonju.review.useranswer.dto.UserAnswerResponseDto;
import com.bonju.review.useranswer.service.UserAnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
@Slf4j
public class UserAnswerController {

  private final UserAnswerService userAnswerService;

  @PostMapping("/{quizId}/answers")
  public ResponseEntity<UserAnswerResponseDto> submit(
          @PathVariable Long quizId,
          @RequestBody @Valid UserAnswerRequestDto dto) {



    SubmitUserAnswerCommand command = new SubmitUserAnswerCommand(quizId, dto.getAnswer(), dto.getDayType());
    // ← 커맨드 값도 한번 더 확인


    UserAnswerResponseDto response = userAnswerService.submitAnswer(command);

    return ResponseEntity.ok(response);
  }
}
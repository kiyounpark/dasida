package com.bonju.review.knowledge_quiz.controller;

import com.bonju.review.knowledge.dto.KnowledgeRegisterRequestDto;
import com.bonju.review.knowledge_quiz.dto.KnowledgeQuizRegistrationResponseDto;
import com.bonju.review.knowledge_quiz.workflow.KnowledgeQuizCreationWorkflow;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/knowledge")
@RequiredArgsConstructor
public class KnowledgeRegistrationController {

  private final KnowledgeQuizCreationWorkflow workflow;

  @PostMapping
  public ResponseEntity<KnowledgeQuizRegistrationResponseDto> register(
          @RequestBody @Valid KnowledgeRegisterRequestDto req
  ) {
    // 워크플로우 메서드가 KnowledgeQuizRegistrationResponseDto 를 반환하도록 변경했다고 가정
    KnowledgeQuizRegistrationResponseDto dto =
            workflow.registerKnowledgeAndGenerateQuizList(req.getTitle(), req.getText());

    // 201 Created 상태로 응답
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(dto);
  }
}

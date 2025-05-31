package com.bonju.review.knowledge_quiz.controller;

import com.bonju.review.knowledge.dto.KnowledgeRegisterRequestDto;
import com.bonju.review.knowledge_quiz.KnowledgeQuizCreationWorkflow;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/knowledge")
@RequiredArgsConstructor
public class KnowledgeRegistrationController {

  private final KnowledgeQuizCreationWorkflow workflow;

  @PostMapping
  public ResponseEntity<Void> register(@RequestBody @Valid KnowledgeRegisterRequestDto req) {
    Long id = workflow.registerKnowledgeAndGenerateQuizList(req.getTitle(), req.getContent());
    return ResponseEntity.created(URI.create("/knowledge/" + id)).build();
  }
}

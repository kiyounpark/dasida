package com.bonju.review.knowledge_quiz.controller;

import com.bonju.review.knowledge.dto.KnowledgeRequestDto;
import com.bonju.review.knowledge_quiz.service.KnowledgeQuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KnowledgeQuizController {
    private final KnowledgeQuizService knowledgeQuizService;

    @PostMapping("/knowledge-quiz")
    public ResponseEntity<Void> registerKnowledgeAndQuiz(@Valid @RequestBody KnowledgeRequestDto knowledgeRequestDto) {
        knowledgeQuizService.registerKnowledgeAndQuiz(knowledgeRequestDto);

        // 201(Created) 상태코드만 반환 (응답 바디 없음)
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

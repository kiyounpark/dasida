package com.bonju.review.controller;

import com.bonju.review.dto.KnowledgeRequestDto;
import com.bonju.review.service.KnowledgeService;
import com.bonju.review.service.OpenAiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
    @RequiredArgsConstructor
    @RequestMapping("/knowledge")
    public class KnowledgeController {

        private final KnowledgeService knowledgeService;
        private final OpenAiService openAiService;

//        @PostMapping
//        public ResponseEntity<String> createKnowledge(@Valid @ModelAttribute KnowledgeRequestDto knowledgeRequestDto) {
//            openAiService.saveQuiz(knowledgeRequestDto);
//
//            // 유효성 검증 후 DTO 데이터 처리
//            return ResponseEntity.ok("Title: " + knowledgeRequestDto.getTitle() + ", Description: " + knowledgeRequestDto.getContentHtml());
//        }

        @PostMapping
        public ResponseEntity<Void> createKnowledge(@Valid @ModelAttribute KnowledgeRequestDto knowledgeRequestDto) {
            knowledgeService.registerKnowledge(knowledgeRequestDto);
            // 201(Created) 상태코드만 반환 (응답 바디 없음)
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }


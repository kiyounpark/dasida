package com.bonju.review.controller;

import com.bonju.review.client.OpenAiClient;
import com.bonju.review.dto.KnowledgeDto;
import com.bonju.review.mapper.QuizEntityMapper;
import com.bonju.review.mapper.QuizJsonParser;
import com.bonju.review.repository.OpenAiRepository;
import com.bonju.review.service.OpenAiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
    @RequiredArgsConstructor
    @RequestMapping("/knowledge")
    public class KnowledgeController {


        private final OpenAiService openAiService;

        @PostMapping
        public ResponseEntity<String> createKnowledge(@Valid @ModelAttribute KnowledgeDto knowledgeDto) {
            openAiService.saveQuiz(knowledgeDto);

            // 유효성 검증 후 DTO 데이터 처리
            return ResponseEntity.ok("Title: " + knowledgeDto.getTitle() + ", Description: " + knowledgeDto.getDescription());
        }
    }


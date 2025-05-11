package com.bonju.review.knowledge.controller;

import com.bonju.review.knowledge.dto.KnowledgeSearchResponseDto;
import com.bonju.review.knowledge.service.KnowledgeSearchService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KnowledgeSearchController {

  private final KnowledgeSearchService knowledgeSearchService;

  @GetMapping("/knowledge/search")
  public KnowledgeSearchResponseDto searchKnowledgeByTitle(@RequestParam @NotBlank String title){
    return knowledgeSearchService.searchKnowledgeByTitle(title);
  }
}

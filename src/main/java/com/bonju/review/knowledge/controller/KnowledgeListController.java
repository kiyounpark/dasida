package com.bonju.review.knowledge.controller;

import com.bonju.review.knowledge.dto.KnowledgeListResponseDto;
import com.bonju.review.knowledge.service.knowledge_list.KnowledgeListService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class KnowledgeListController {

  private final KnowledgeListService knowledgeListService;

  @GetMapping("/knowledge")
  public KnowledgeListResponseDto getKnowledgeList(
          @RequestParam(defaultValue = "0") @Min(0) int offset) {
    return knowledgeListService.getKnowledgeList(offset);
  }
}

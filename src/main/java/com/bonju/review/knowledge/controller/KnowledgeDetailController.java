package com.bonju.review.knowledge.controller;

import com.bonju.review.knowledge.dto.KnowledgeDetailResponseDto;
import com.bonju.review.knowledge.service.KnowledgeReadService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KnowledgeDetailController {

  private final KnowledgeReadService knowledgeReadService;

  @GetMapping("/knowledge/{id}")
  public KnowledgeDetailResponseDto xx(@PathVariable @Min(1) Long id){
    return knowledgeReadService.getKnowledgeById(id);
  }
}

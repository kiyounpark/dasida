package com.bonju.review.knowledge.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class KnowledgeResponseDto {
  private Long id;
  private String title;
}

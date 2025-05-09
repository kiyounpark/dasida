package com.bonju.review.knowledge.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class KnowledgeItemResponseDto {
  private Long id;
  private String title;
  private LocalDateTime createAt;
}

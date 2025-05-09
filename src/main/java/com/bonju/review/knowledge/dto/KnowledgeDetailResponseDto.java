package com.bonju.review.knowledge.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class KnowledgeDetailResponseDto {
  private Long id;
  private String title;
  private String content;
  private LocalDateTime createdAt;
}

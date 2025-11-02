package com.bonju.review.knowledge.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class KnowledgeDetailResponseDto {
  private Long id;
  private String title;
  private String text;
  private List<String> images;
  private LocalDateTime createdAt;
}

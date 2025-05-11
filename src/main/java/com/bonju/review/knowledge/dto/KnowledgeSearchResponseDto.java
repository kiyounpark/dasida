package com.bonju.review.knowledge.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class KnowledgeSearchResponseDto {

  private final List<KnowledgeItemResponseDto> list;

}
package com.bonju.review.knowledge.dto;

import com.bonju.review.util.dto.PagingResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class KnowledgeListResponseDto {
  private List<KnowledgeItemResponseDto> knowledgeList;
  private PagingResponseDto page;
}

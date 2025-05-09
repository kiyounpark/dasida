package com.bonju.review.util.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PagingResponseDto {
  private Integer nextOffset;
}

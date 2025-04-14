package com.bonju.review.knowledge.vo;


import com.bonju.review.knowledge.exception.KnowledgeException;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class SingleDayRange {

  private final LocalDateTime start;
  private final LocalDateTime end;

  public SingleDayRange(LocalDate baseDate) {
    if(baseDate == null){
      throw new KnowledgeException("baseDate가 null일수 없습니다.");
    }
    this.start = baseDate.atStartOfDay();
    this.end = this.start.plusDays(1);
  }
}

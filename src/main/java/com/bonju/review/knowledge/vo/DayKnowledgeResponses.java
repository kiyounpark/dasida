package com.bonju.review.knowledge.vo;

import com.bonju.review.knowledge.converter.MarkdownConverter;
import com.bonju.review.knowledge.dto.DayKnowledgeResponseDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.repository.knowledges.KnowledgesRepository;
import com.bonju.review.user.entity.User;
import com.bonju.review.util.enums.DayType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DayKnowledgeResponses {

  private final List<DayKnowledgeResponseDto> responses;

  private DayKnowledgeResponses(List<DayKnowledgeResponseDto> responses) {
    this.responses = responses;
  }

  public static DayKnowledgeResponses from(User user, KnowledgesRepository repository, MarkdownConverter converter) {
    List<DayKnowledgeResponseDto> list = new ArrayList<>();

    for (DayType dayType : DayType.values()) {
      int daysAgo = dayType.getDaysAgo();
      List<Knowledge> knowledgeList = repository.findKnowledgesByDaysAgo(user, daysAgo);

      for (Knowledge knowledge : knowledgeList) {
        String truncatedParagraphText = converter.convertTruncatedParagraph(knowledge.getContent());

        list.add(
                DayKnowledgeResponseDto.builder()
                .dayType(dayType.getDaysAgo())
                .id(knowledge.getId())
                .title(knowledge.getTitle())
                .content(truncatedParagraphText)
                .build());
      }
    }

    return new DayKnowledgeResponses(list);
  }

  public List<DayKnowledgeResponseDto> asList() {
    return Collections.unmodifiableList(responses);
  }
}

// 여기서 발생할수 있는 예외는 뭐가 있을까?







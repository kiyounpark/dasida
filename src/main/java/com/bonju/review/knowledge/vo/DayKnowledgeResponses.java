package com.bonju.review.knowledge.vo;

import com.bonju.review.knowledge.converter.MarkdownConverter;
import com.bonju.review.knowledge.dto.DayKnowledgeResponseDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.repository.knowledges.KnowledgesRepository;
import com.bonju.review.user.entity.User;
import com.bonju.review.util.enums.DayType;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class DayKnowledgeResponses {

  private final ImmutableList<DayKnowledgeResponseDto> responses;

  private DayKnowledgeResponses(ImmutableList<DayKnowledgeResponseDto> responses) {
    this.responses = responses;
  }

  public static DayKnowledgeResponses from(User user, KnowledgesRepository repository, MarkdownConverter converter) {
    ImmutableList.Builder<DayKnowledgeResponseDto> listBuilder = ImmutableList.builder();

    for (DayType dayType : DayType.values()) {
      int daysAgo = dayType.getDaysAgo();
      List<Knowledge> knowledgeList = repository.findKnowledgesByDaysAgo(user, daysAgo);

      for (Knowledge knowledge : knowledgeList) {
        String truncatedParagraphText = converter.convertTruncatedParagraph(knowledge.getContent());

        listBuilder.add(
                DayKnowledgeResponseDto.builder()
                        .dayType(dayType.getDaysAgo())
                        .id(knowledge.getId())
                        .title(knowledge.getTitle())
                        .content(truncatedParagraphText)
                        .build());
      }
    }

    return new DayKnowledgeResponses(listBuilder.build()); // ✅ ImmutableList 생성
  }

  public ImmutableList<DayKnowledgeResponseDto> asList() { // ✅ 반환도 ImmutableList
    return responses;
  }
}






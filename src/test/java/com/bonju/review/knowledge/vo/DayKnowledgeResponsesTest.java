package com.bonju.review.knowledge.vo;

import com.bonju.review.BaseTest;
import com.bonju.review.knowledge.dto.DayKnowledgeResponseDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.user.entity.User;
import com.bonju.review.util.enums.DayType;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class DayKnowledgeResponsesTest extends BaseTest {

  @Test
  @DisplayName("각 DayType마다 repository가 올바른 Knowledge를 반환하면, converter를 통해 올바른 DTO가 생성되어야 한다")
  @Transactional
  void shouldCreateDtoList_whenKnowledgeListExistForEachDayType() {
    // given
    User user = User.builder()
            .nickname("박기윤")
            .kakaoId("12324").build();

    userRepository.save(user);

    for (DayType dayType : DayType.values()) {
      int daysAgo = dayType.getDaysAgo();

      Knowledge knowledge = Knowledge.builder()
              .user(user)
              .title("Title " + daysAgo)
              .content("Content  " + daysAgo)
              .createdAt(LocalDateTime.now().minusDays(daysAgo))
              .build();

      knowledgeRegisterRepository.registerKnowledge(knowledge);
    }


    // when
    DayKnowledgeResponses responses = DayKnowledgeResponses.from(user, knowledgesRepository, markdownConverter);
    ImmutableList<DayKnowledgeResponseDto> dtoCollections = responses.asImmutableList();

    // then
    int expectedSize = DayType.values().length;

    assertThat(dtoCollections).hasSize(expectedSize);

    for (DayKnowledgeResponseDto dto : dtoCollections) {
      int daysAgo = dto.getDayType();
      assertThat(dto.getTitle()).isEqualTo("Title " + daysAgo);
      assertThat(dto.getContent()).isEqualTo("Content " + daysAgo);
    }
  }
}

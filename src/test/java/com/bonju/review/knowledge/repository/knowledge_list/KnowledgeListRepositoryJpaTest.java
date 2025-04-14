package com.bonju.review.knowledge.repository.knowledge_list;

import com.bonju.review.BaseTest;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.vo.SingleDayRange;
import com.bonju.review.user.entity.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KnowledgeListRepositoryJpaTest extends BaseTest {

  // 테스트 기준 날짜 (2023-04-10 기준)
  private static final LocalDate REFERENCE_DATE = LocalDate.of(2023, 4, 10);

  @DisplayName("지정한 사용자로부터 정확히 '2일 전(2023-04-08)'에 생성된 Knowledge만 조회한다")
  @Test
  @Transactional
  void findKnowledgeListByDaysAgo_shouldReturnEntitiesOfTheSpecifiedDay() {
    // given
    User user = createAndPersistUser("kakaoId-123", "tester");

    LocalDate baseDate = REFERENCE_DATE.minusDays(2); // 기준일: 2023-04-08
    SingleDayRange range = new SingleDayRange(baseDate); // 2023-04-08 00:00 ~ 2023-04-09 00:00

    // 2023-04-07 23:59 - 조회 대상 아님
    createAndPersistKnowledge(user, range.getStart().minusMinutes(1));

    // 2023-04-08 10:00 - 조회 대상
    Knowledge expected = createAndPersistKnowledge(user, range.getStart().plusHours(10));

    // 2023-04-09 00:00 - 조회 대상 아님
    createAndPersistKnowledge(user, range.getEnd());

    // when
    List<Knowledge> result = knowledgeListRepository.findKnowledgeListByDateRange(user, range);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getId()).isEqualTo(expected.getId());
    assertThat(result.getFirst().getCreatedAt()).isEqualTo(expected.getCreatedAt());
  }

  private User createAndPersistUser(String kakaoId, String nickname) {
    User user = User.builder()
            .kakaoId(kakaoId)
            .nickname(nickname)
            .build();
    em.persist(user);
    return user;
  }

  private Knowledge createAndPersistKnowledge(User user, LocalDateTime createdAt) {
    Knowledge knowledge = Knowledge.builder()
            .user(user)
            .title("Test Title")
            .content("Test Content")
            .createdAt(createdAt)
            .build();
    em.persist(knowledge);
    return knowledge;
  }
}

package com.bonju.review.knowledge.service;

import com.bonju.review.knowledge.dto.KnowledgeSearchResponseDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.repository.KnowledgeSearchRepository;
import com.bonju.review.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

class KnowledgeSearchServiceTest {

  public static final LocalDateTime FIXED_DATE = LocalDateTime.of(2025, 5, 10, 0, 0);
  @Mock
  KnowledgeSearchRepository knowledgeSearchRepository;

  @InjectMocks
  KnowledgeSearchServiceImpl knowledgeSearchService;

  @DisplayName("제목 키워드로 검색하면 지정한 개수만큼 반환한다")
  @Test
  void shouldReturnTwoItems_WhenTitlesContainKeyword() {
    // given
    User mockUser = createMockUser();
    List<Knowledge> knowledgeList = List.of(
            createMockKnowledge(mockUser, "제목1"),
            createMockKnowledge(mockUser, "제목2")
    );
    given(knowledgeSearchRepository.findByTitleContaining(anyString()))
            .willReturn(knowledgeList);

    // when
    KnowledgeSearchResponseDto result =
            knowledgeSearchService.searchKnowledgeByTitle("제목");

    // then
    assertThat(result.getList()).hasSize(2);
  }

  private Knowledge createMockKnowledge(User user, String title) {
    Knowledge knowledge = Knowledge.builder()
            .user(user)
            .title(title)
            .content("내용")
            .createdAt(FIXED_DATE)

            .build();

    ReflectionTestUtils.setField(knowledge, "id", 1L);
    return knowledge;
  }

  private User createMockUser() {
    return User.builder()
            .kakaoId("123")
            .nickname("박기윤")
            .build();
  }
}

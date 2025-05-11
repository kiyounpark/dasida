package com.bonju.review.knowledge.service;

import com.bonju.review.knowledge.dto.KnowledgeItemResponseDto;
import com.bonju.review.knowledge.dto.KnowledgeSearchResponseDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.repository.KnowledgeSearchRepository;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class KnowledgeSearchServiceTest {

  public static final LocalDateTime FIXED_DATE = LocalDateTime.of(2025, 5, 10, 0, 0);
  public static final String SEARCH_TITLE = "제목";

  @Mock
  KnowledgeSearchRepository knowledgeSearchRepository;

  @Mock
  UserService userService;

  @InjectMocks
  KnowledgeSearchServiceImpl knowledgeSearchService;

  @Nested
  @DisplayName("검색 결과가 없을 때")
  class EmptyResultTests {

    @Test
    @DisplayName("빈 리스트를 반환한다")
    void shouldReturnEmptyList_WhenNoTitleMatches() {
      // given
      User mockUser = createMockUser();
      given(userService.findUser()).willReturn(mockUser);
      given(knowledgeSearchRepository.findByTitleContaining(any(), anyString()))
              .willReturn(List.of());

      // when
      KnowledgeSearchResponseDto result = knowledgeSearchService.searchKnowledgeByTitle("없는 제목");

      // then
      assertThat(result.getList()).isEmpty();
    }
  }

  @Nested
  @DisplayName("정상적으로 검색될 때")
  class ValidSearchTests {

    @Test
    @DisplayName("지정한 개수만큼 반환한다")
    void shouldReturnTwoItems_WhenTitlesContainKeyword() {
      // given
      User mockUser = createMockUser();
      List<Knowledge> knowledgeList = List.of(
              createKnowledgeWithId(mockUser, SEARCH_TITLE + "1", 1L),
              createKnowledgeWithId(mockUser, SEARCH_TITLE + "2", 2L)
      );

      given(userService.findUser()).willReturn(mockUser);
      given(knowledgeSearchRepository.findByTitleContaining(any(), anyString()))
              .willReturn(knowledgeList);

      // when
      KnowledgeSearchResponseDto result = knowledgeSearchService.searchKnowledgeByTitle(SEARCH_TITLE);

      // then
      assertThat(result.getList()).hasSize(2);
    }
  }

  @Nested
  @DisplayName("검색 결과의 필드가 정확할 때")
  class FieldMappingTests {

    @Test
    @DisplayName("각 항목의 값이 정확하게 매핑된다")
    void shouldReturnCorrectValuesInResponse() {
      // given
      User mockUser = createMockUser();
      String knowledgeTitle = SEARCH_TITLE + "1";
      long knowledgeId = 100L;
      Knowledge knowledge = createKnowledgeWithId(mockUser, knowledgeTitle, knowledgeId);

      given(userService.findUser()).willReturn(mockUser);
      given(knowledgeSearchRepository.findByTitleContaining(any(), anyString()))
              .willReturn(List.of(knowledge));

      // when
      KnowledgeSearchResponseDto result = knowledgeSearchService.searchKnowledgeByTitle(SEARCH_TITLE);

      // then
      KnowledgeItemResponseDto item = result.getList().getFirst();
      assertThat(item.getId()).isEqualTo(knowledgeId);
      assertThat(item.getTitle()).isEqualTo(knowledgeTitle);
      assertThat(item.getCreateAt()).isEqualTo(FIXED_DATE);
    }
  }

  private User createMockUser() {
    return User.builder()
            .kakaoId("123")
            .nickname("박기윤")
            .build();
  }

  private Knowledge createKnowledge(User user, String title) {
    return Knowledge.builder()
            .user(user)
            .title(title)
            .content("내용")
            .createdAt(FIXED_DATE)
            .build();
  }

  private Knowledge createKnowledgeWithId(User user, String title, Long id) {
    Knowledge knowledge = createKnowledge(user, title);
    ReflectionTestUtils.setField(knowledge, "id", id);
    return knowledge;
  }
}

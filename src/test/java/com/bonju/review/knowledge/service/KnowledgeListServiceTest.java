package com.bonju.review.knowledge.service;

import com.bonju.review.knowledge.dto.KnowledgeListResponseDto;
import com.bonju.review.knowledge.dto.KnowledgeItemResponseDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.exception.KnowledgeException;
import com.bonju.review.knowledge.repository.KnowledgeListRepository;
import com.bonju.review.knowledge.service.knowledge_list.KnowledgeListServiceImpl;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import com.bonju.review.util.dto.PagingResponseDto;
import com.bonju.review.util.enums.error_code.KnowledgeErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("KnowledgeListService 단위 테스트")
class KnowledgeListServiceTest {

  /* ── SUT & Mock ────────────────────────────────────────────────────── */
  @Mock   KnowledgeListRepository knowledgeListRepository;
  @Mock   UserService userService;
  @InjectMocks KnowledgeListServiceImpl knowledgeListService;

  /* ── Fixture 상수 ──────────────────────────────────────────────────── */
  private static final int LIMIT = 10;
  private static final User USER = User.builder()
          .kakaoId("123").nickname("박기윤").build();
  private static final LocalDateTime BASE_TIME = LocalDateTime.of(2025, 5, 5, 0, 0);
  private static final String TITLE = "지식 제목 ";
  private static final String CONTENT = "지식 내용 ";

  /* ── Fixture 헬퍼 ──────────────────────────────────────────────────── */
  private List<Knowledge> generateEntities(int count) {
    return IntStream.rangeClosed(1, count)
            .mapToObj(i -> Knowledge.builder()
                    .user(USER)
                    .title(TITLE + i)
                    .text(CONTENT + i)
                    .createdAt(BASE_TIME.plusDays(i))
                    .build())
            .toList();
  }

  private void stub(int offset, int size) {
    given(userService.findUser()).willReturn(USER);

    given(knowledgeListRepository.findKnowledgeList(USER ,offset, LIMIT + 1))
            .willReturn(generateEntities(size));
  }

  /* ── Tests ─────────────────────────────────────────────────────────── */
  @Test
  @DisplayName("지식 3건이 반환되면, 응답 리스트 크기도 3이다")
  void returns_sameSizeList() {
    stub(0, 3);

    KnowledgeListResponseDto result = knowledgeListService.getKnowledgeList(0);

    assertThat(result.getKnowledgeList()).hasSize(3);
  }

  @Test
  @DisplayName("첫 번째 지식 엔티티의 필드가 응답 DTO에 정확히 매핑된다")
  void maps_firstEntityFields() {
    stub(0, 3);

    KnowledgeItemResponseDto first = knowledgeListService.getKnowledgeList(0)
            .getKnowledgeList().getFirst();

    assertThat(first.getTitle()).isEqualTo(TITLE + 1);
  }

  @Test
  @DisplayName("조회 결과가 LIMIT 이하라면, nextOffset은 null을 반환한다")
  void computes_nextOffset() {
    int offset = 20;
    stub(offset, 3);

    PagingResponseDto pagingResponseDto = knowledgeListService.getKnowledgeList(offset)
            .getPage();

    assertThat(pagingResponseDto.getNextOffset()).isNull();
  }

  @Test
  @DisplayName("Repository에서 DataAccessException이 발생하면 KnowledgeException으로 감싼다")
  void throws_KnowledgeException_when_DataAccessException_occurs() {
    // given
    given(userService.findUser()).willReturn(USER);
    given(knowledgeListRepository.findKnowledgeList(USER, 0, LIMIT + 1))
            .willThrow(new DataAccessResourceFailureException("의도된 예외"));

    // when & then
    assertThatThrownBy(() -> knowledgeListService.getKnowledgeList(0))
            .isInstanceOf(KnowledgeException.class)
            .hasMessageContaining(KnowledgeErrorCode.RETRIEVE_FAILED.getMessage());
  }

  @Nested
  @DisplayName("다음 페이지 유무 계산 (nextOffset)")
  class IsNext {

    @Test
    @DisplayName("조회 결과 수가 LIMIT 미만이면, nextOffset은 null이다")
    void returns_null_whenLessThanLimit() {
      stub(0, 7);

      PagingResponseDto pagingResponseDto = knowledgeListService.getKnowledgeList(0)
              .getPage();

      assertThat(pagingResponseDto.getNextOffset()).isNull();
    }

    @Test
    @DisplayName("조회 결과 수가 LIMIT 초과이면, nextOffset은 null이 아니다")
    void returns_value_whenOverLimit() {
      stub(0, 11); // LIMIT + 1

      Integer nextOffset = knowledgeListService.getKnowledgeList(0)
              .getPage().getNextOffset();

      assertThat(nextOffset).isNotNull();
    }
  }
}

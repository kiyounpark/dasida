package com.bonju.review.knowledge.controller;

import com.bonju.review.exception.ErrorResponse;
import com.bonju.review.knowledge.dto.KnowledgeItemResponseDto;
import com.bonju.review.knowledge.dto.KnowledgeSearchResponseDto;
import com.bonju.review.knowledge.exception.KnowledgeException;
import com.bonju.review.knowledge.service.KnowledgeSearchService;
import com.bonju.review.slack.SlackErrorMessageFactory;
import com.bonju.review.testsupport.security.WithMockKakaoUser;
import com.bonju.review.util.enums.error_code.KnowledgeErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(KnowledgeSearchController.class)
class KnowledgeSearchControllerTest {

  public static final LocalDateTime FIXED_DATE = LocalDateTime.of(2025, 5, 11, 0, 0);
  public static final String PATH = "/knowledge/search";
  public static final String QUERY_PARAMETER_KEY = "title";
  public static final String SEARCH_TITLE = "제목";

  @MockitoBean
  KnowledgeSearchService knowledgeSearchService;

  @MockitoBean
  SlackErrorMessageFactory slackErrorMessageFactory;

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Nested
  @DisplayName("제목 검색 성공 케이스")
  class SearchSuccessTest {

    @DisplayName("제목 검색 시, 2건을 반환한다")
    @Test
    @WithMockKakaoUser
    void returnTwoKnowledgeItems_WhenSearchByTitle() throws Exception {
      // given
      KnowledgeItemResponseDto dto1 = createKnowledgeResponseDto(1L, SEARCH_TITLE);
      KnowledgeItemResponseDto dto2 = createKnowledgeResponseDto(2L, SEARCH_TITLE + " 1");

      mockSearchResult(List.of(dto1, dto2));

      // when & then
      KnowledgeSearchResponseDto actual = performSearchRequest(status().isOk(), KnowledgeSearchResponseDto.class);
      assertThat(actual.getList()).hasSize(2);
    }

    @DisplayName("제목 검색시 정확한 리스트를 반환하는지 확인한다.")
    @Test
    @WithMockKakaoUser
    void returnCorrectKnowledgeItem_WhenSearchByTitle() throws Exception {
      // given
      KnowledgeItemResponseDto dto = createKnowledgeResponseDto(1L, SEARCH_TITLE);

      mockSearchResult(List.of(dto));

      // when && then
      KnowledgeSearchResponseDto actual = performSearchRequest(status().isOk(), KnowledgeSearchResponseDto.class);
      KnowledgeItemResponseDto result = actual.getList().getFirst();

      assertThat(result.getId()).isEqualTo(1L);
      assertThat(result.getTitle()).isEqualTo(SEARCH_TITLE);
      assertThat(result.getCreateAt()).isEqualTo(FIXED_DATE);
    }
  }

  @Nested
  @DisplayName("지식 검색 실패 케이스")
  class SearchFailTest {

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    @DisplayName("쿼리 파라미터의 요청이 잘못되면 상태코드 400 status 를 반환한다")
    @WithMockKakaoUser
    void returnBadRequest_WhenTitleParamIsBlankOrInvalid(String invalidTitle) throws Exception {
      // when & then
      mockMvc.perform(get(PATH).param(QUERY_PARAMETER_KEY, invalidTitle))
              .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockKakaoUser
    @DisplayName("KnowledgeSearchService 에서 빈 리스트를 반환하면 빈 리스트를 반환한다.")
    void returnEmptyList_WhenSearchResultIsEmpty() throws Exception {
      // given
      mockSearchResult(List.of());

      // when
      KnowledgeSearchResponseDto actual = performSearchRequest(status().isOk(), KnowledgeSearchResponseDto.class);

      // then
      assertThat(actual.getList()).isEmpty();
    }

    @Test
    @WithMockKakaoUser
    @DisplayName("KnowledgeSearchService에서 KnowledgeException이 발생하면 ErrorResponse에 예외가 담기는지 확인한다")
    void should_return_error_response_and_send_slack_when_knowledge_exception_thrown() throws Exception {
      given(knowledgeSearchService.searchKnowledgeByTitle(anyString())).willThrow(new KnowledgeException(KnowledgeErrorCode.RETRIEVE_FAILED));
      given(slackErrorMessageFactory.createErrorMessage(any(), any())).willReturn("지식 검색 예외 발생 테스트");

      ErrorResponse errorResponse = performSearchRequest(status().is5xxServerError(), ErrorResponse.class);

      assertThat(errorResponse.getStatus()).isEqualTo(KnowledgeErrorCode.RETRIEVE_FAILED.getHttpStatus().value());
      assertThat(errorResponse.getError()).isEqualTo(KnowledgeErrorCode.RETRIEVE_FAILED.getHttpStatus().getReasonPhrase());
      assertThat(errorResponse.getMessage()).isEqualTo(KnowledgeErrorCode.RETRIEVE_FAILED.getMessage());
      assertThat(errorResponse.getPath()).isEqualTo(PATH);
      assertThat(errorResponse.getTimestamp()).isNotNull();

      verify(slackErrorMessageFactory).createErrorMessage(any(), any());
    }
  }

  // ------ 헬퍼 메서드 -----

  private KnowledgeItemResponseDto createKnowledgeResponseDto(Long id, String title) {
    return KnowledgeItemResponseDto.builder()
            .id(id)
            .title(title)
            .createAt(FIXED_DATE)
            .build();
  }


  private void mockSearchResult(List<KnowledgeItemResponseDto> responseList) {
    KnowledgeSearchResponseDto dto = KnowledgeSearchResponseDto.builder()
            .list(responseList)
            .build();

    given(knowledgeSearchService.searchKnowledgeByTitle(anyString())).willReturn(dto);
  }

  private <T> T performSearchRequest(ResultMatcher status, Class<T> clazz) throws Exception {
    String response = mockMvc.perform(get(PATH).param(QUERY_PARAMETER_KEY, SEARCH_TITLE))
            .andExpect(status)
            .andReturn()
            .getResponse()
            .getContentAsString();

    return objectMapper.readValue(response, clazz);
  }
}


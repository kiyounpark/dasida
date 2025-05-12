package com.bonju.review.knowledge.controller;

import com.bonju.review.exception.ErrorResponse;
import com.bonju.review.knowledge.dto.KnowledgeListResponseDto;
import com.bonju.review.knowledge.dto.KnowledgeItemResponseDto;
import com.bonju.review.knowledge.exception.KnowledgeException;
import com.bonju.review.knowledge.service.knowledge_list.KnowledgeListService;
import com.bonju.review.slack.SlackErrorMessageFactory;
import com.bonju.review.util.dto.PagingResponseDto;
import com.bonju.review.util.enums.error_code.KnowledgeErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KnowledgeListController.class)
class KnowledgeListControllerTest {

  private static final String KNOWLEDGE_LIST_END_POINT = "/knowledge";

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockitoBean
  KnowledgeListService knowledgeListService;

  @MockitoBean
  SlackErrorMessageFactory slackErrorMessageFactory;

  private static KnowledgeListResponseDto getKnowledgeListResponseDto() {
    KnowledgeItemResponseDto knowledgeItemResponseDto = KnowledgeItemResponseDto.builder()
            .id((long) 1)
            .title("제목")
            .build();


    PagingResponseDto pagingResponseDto = PagingResponseDto.builder()
            .nextOffset(10)
            .build();

    return KnowledgeListResponseDto.builder()
            .knowledgeList(List.of(knowledgeItemResponseDto))
            .page(pagingResponseDto)
            .build();
  }

  @DisplayName("쿼리 파라미터 없이 목록 조회 시 200 OK와 리스트 및 페이징 정보를 반환한다")
  @WithMockUser
  @Test
  void getKnowledgeList_ReturnsKnowledgeListAndPagingInfo() throws Exception {
    //given
    KnowledgeListResponseDto responseDto = getKnowledgeListResponseDto();

    given(knowledgeListService.getKnowledgeList(anyInt())).willReturn(responseDto);

    //when & then

    mockMvc.perform(get(KNOWLEDGE_LIST_END_POINT))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.knowledge_list.length()").value(1))
            .andExpect(jsonPath("$.knowledge_list[0].title").value("제목"))
            .andExpect(jsonPath("$.page.next_offset").value(10));
  }

  @DisplayName("만약 쿼리스트링이 0 미만이면 예외가 발생한다.")
  @Test
  @WithMockUser
  void getKnowledgeList_WhenOffsetIsNegative_ReturnsBadRequest() throws Exception {
    //given
    int offset = -1;
    KnowledgeListResponseDto knowledgeListResponseDto = getKnowledgeListResponseDto();
    given(knowledgeListService.getKnowledgeList(anyInt())).willReturn(knowledgeListResponseDto);
    // when & then
    mockMvc.perform(get(KNOWLEDGE_LIST_END_POINT)
                    .param("offset", String.valueOf(offset)))
            .andExpect(status().isBadRequest());

  }

  @DisplayName("KnowledgeListService에서 예외가 발생하면 ErrorResponse에 예외가 담기고 슬랙 메시지가 전송된다")
  @WithMockUser
  @Test
  void returns_error_response_when_service_throws_exception() throws Exception {
    // given
    given(knowledgeListService.getKnowledgeList(anyInt()))
            .willThrow(new KnowledgeException(KnowledgeErrorCode.RETRIEVE_FAILED));
    given(slackErrorMessageFactory.createErrorMessage(any(), any()))
            .willReturn("목록 조회 중 예외 발생 테스트");

    // when
    String response = mockMvc.perform(get(KNOWLEDGE_LIST_END_POINT))
            .andExpect(status().isInternalServerError())
            .andReturn()
            .getResponse()
            .getContentAsString();

    // then
    ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);

    assertThat(errorResponse.getStatus()).isEqualTo(KnowledgeErrorCode.RETRIEVE_FAILED.getHttpStatus().value());
    assertThat(errorResponse.getMessage()).isEqualTo(KnowledgeErrorCode.RETRIEVE_FAILED.getMessage());
    assertThat(errorResponse.getError()).isEqualTo(KnowledgeErrorCode.RETRIEVE_FAILED.getHttpStatus().getReasonPhrase());
    assertThat(errorResponse.getPath()).isEqualTo(KNOWLEDGE_LIST_END_POINT);
    assertThat(errorResponse.getTimestamp()).isNotNull();

    // 슬랙 메시지 전송 여부 확인
    verify(slackErrorMessageFactory).createErrorMessage(any(), any());
  }
}

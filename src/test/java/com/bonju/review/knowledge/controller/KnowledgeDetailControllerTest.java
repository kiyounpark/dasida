package com.bonju.review.knowledge.controller;

import com.bonju.review.exception.ErrorResponse;
import com.bonju.review.knowledge.dto.KnowledgeDetailResponseDto;
import com.bonju.review.knowledge.exception.KnowledgeException;
import com.bonju.review.knowledge.service.KnowledgeReadService;
import com.bonju.review.util.enums.error_code.KnowledgeErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KnowledgeDetailController.class)
@ActiveProfiles("test")
class KnowledgeDetailControllerTest {
  private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 5, 10, 0, 0,0);
  public static final String KNOWLEDGE_TITLE = "지식 제목";
  public static final String KNOWLEDGE_CONTENT = "지식 내용";
  public static final String PATH = "/knowledge/1";
  public static final String BAD_PATH = "/knowledge/0";

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockitoBean
  KnowledgeReadService knowledgeReadService;

  @DisplayName("프론트엔드에서 요청한 지식 id 값이 존재한다면 KnowledgeDetailResponseDto를 반환한다.")
  @Test
  @WithMockUser
  void shouldReturnKnowledgeDetailResponseDto() throws Exception {
    // given
    KnowledgeDetailResponseDto expected = KnowledgeDetailResponseDto.builder()
            .id(1L)
            .title(KNOWLEDGE_TITLE)
            .content(KNOWLEDGE_CONTENT)
            .createdAt(FIXED_TIME)
            .build();

    given(knowledgeReadService.getKnowledgeById(anyLong())).willReturn(expected);

    // when
    String response = mockMvc.perform(get(PATH))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    // then
    KnowledgeDetailResponseDto actual = objectMapper.readValue(response, KnowledgeDetailResponseDto.class);

    assertThat(actual.getId()).isEqualTo(expected.getId());
    assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
    assertThat(actual.getContent()).isEqualTo(expected.getContent());
    assertThat(actual.getCreatedAt()).isEqualTo(expected.getCreatedAt());
  }

  @DisplayName("프론트엔드에서 요청한 지식 id 값이 DB에 없다면, 404 예외를 던진다.")
  @WithMockUser
  @Test
  void shouldReturn404WhenKnowledgeNotFound() throws Exception {
    // given
    given(knowledgeReadService.getKnowledgeById(anyLong()))
            .willThrow(new KnowledgeException(KnowledgeErrorCode.NOT_FOUND));

    //when & then
    String response = mockMvc.perform(get(PATH))
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse()
            .getContentAsString();

    ErrorResponse actual = objectMapper.readValue(response, ErrorResponse.class);

    verify(knowledgeReadService, times(1)).getKnowledgeById(anyLong());
    assertThat(actual.getMessage()).isEqualTo(KnowledgeErrorCode.NOT_FOUND.getMessage());
    assertThat(actual.getStatus()).isEqualTo(KnowledgeErrorCode.NOT_FOUND.getHttpStatus().value());
    assertThat(actual.getPath()).isEqualTo(PATH);
    assertThat(actual.getError()).isEqualTo(KnowledgeErrorCode.NOT_FOUND.getHttpStatus().getReasonPhrase());
  }

  @DisplayName("id가 1 미만이면 400을 반환하고 서비스는 호출되지 않는다")
  @Test
  @WithMockUser
  void shouldReturn400_WhenIdIsLessThan1() throws Exception {
    //when & then
    mockMvc.perform(get(BAD_PATH))
            .andExpect(status().isBadRequest());


    verifyNoInteractions(knowledgeReadService);
  }
}

package com.bonju.review.knowledge.controller;

import com.bonju.review.knowledge.dto.KnowledgeListResponseDto;
import com.bonju.review.knowledge.dto.KnowledgeResponseDto;
import com.bonju.review.knowledge.service.knowledge_list.KnowledgeListService;
import com.bonju.review.util.dto.PagingResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KnowledgeListController.class)
@ActiveProfiles("test")
class KnowledgeListControllerTest {

  private static final String KNOWLEDGE_LIST_END_POINT = "/knowledge";

  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  KnowledgeListService knowledgeListService;

  private static KnowledgeListResponseDto getKnowledgeListResponseDto() {
    KnowledgeResponseDto knowledgeResponseDto = KnowledgeResponseDto.builder()
            .id((long) 1)
            .title("제목")
            .build();


    PagingResponseDto pagingResponseDto = PagingResponseDto.builder()
            .nextOffset(10)
            .build();

    return KnowledgeListResponseDto.builder()
            .knowledgeList(List.of(knowledgeResponseDto))
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


}

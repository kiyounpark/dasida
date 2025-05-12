package com.bonju.review.knowledge.Integration;

import com.bonju.review.knowledge.dto.KnowledgeItemResponseDto;
import com.bonju.review.knowledge.dto.KnowledgeSearchResponseDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.testsupport.security.WithMockKakaoUser;
import com.bonju.review.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class KnowledgeSearchIntegrationTest {

  public static final String KAKAO_ID = "1";
  public static final LocalDateTime FIXED_DATE = LocalDateTime.of(2025, 5, 12, 0, 0);
  public static final String URI_TEMPLATE = "/knowledge/search";
  public static final String QUERY_STRING_KEY = "title";
  public static final String SEARCH_TITLE = "제목";
  @Autowired
  EntityManager em;

  @Autowired ObjectMapper objectMapper;

  @Autowired MockMvc mockMvc;

  @AfterEach
  void clear(){
    em.flush();
    em.clear();
  }

  @Nested
  @DisplayName("성공 테스트 케이스")
  class SuccessTest {
    @Test
    @DisplayName("지식 검색시 검증된 리스트를 반환한다")
    @WithMockKakaoUser(kakaoId = KAKAO_ID)
    void shouldReturnTwoResults_WhenSearchingByTitle() throws Exception {
      //given
      User user = persistKnowledge();
      persistKnowledge(user, "지식 제목 1");
      persistKnowledge(user, "지식 제목 2");

      //when & then
      String responseString = mockMvc.perform(get(URI_TEMPLATE).param(QUERY_STRING_KEY, SEARCH_TITLE))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse()
              .getContentAsString();

      KnowledgeSearchResponseDto response = objectMapper.readValue(responseString, KnowledgeSearchResponseDto.class);
      assertThat(response.getList()).hasSize(2);
    }

    @DisplayName("지식 검색시 정확한 값을 반환하는지 확인한다.")
    @Test
    @WithMockKakaoUser(kakaoId = KAKAO_ID)
     void shouldReturnExactTitle_WhenSearchMatches() throws Exception {
      //given
      User user = persistKnowledge();
      String knowledgeTitle = "지식 제목 1";
      persistKnowledge(user, knowledgeTitle);

      //when & then
      String responseString = mockMvc.perform(get(URI_TEMPLATE).param(QUERY_STRING_KEY, SEARCH_TITLE))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse()
              .getContentAsString();

      KnowledgeSearchResponseDto actualList = objectMapper.readValue(responseString, KnowledgeSearchResponseDto.class);
      KnowledgeItemResponseDto response = actualList.getList().getFirst();
      assertThat(response.getTitle()).isEqualTo(knowledgeTitle);
    }
  }

  @Nested
  @DisplayName("실패 테스트 케이스")
  class FailTest {

    @DisplayName("쿼리 파라미터 없이 요청하면 400을 반환한다")
    @Test
    @WithMockKakaoUser(kakaoId = KAKAO_ID)
    void shouldReturn400_WhenQueryParamMissing() throws Exception {
      mockMvc.perform(get(URI_TEMPLATE))
              .andExpect(status().isBadRequest());
    }

    @DisplayName("공백 제목으로 검색 시 400을 반환한다")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t", "\n"})
    @WithMockKakaoUser(kakaoId = KAKAO_ID)
    void shouldReturn400_WhenQueryParamBlank(String invalidTitle) throws Exception {
      mockMvc.perform(get(URI_TEMPLATE).param(QUERY_STRING_KEY, invalidTitle))
              .andExpect(status().isBadRequest());
    }

    @DisplayName("로그인하지 않고 요청하면 401을 반환한다")
    @Test
    void shouldReturn401_WhenNotAuthenticated() throws Exception {
      mockMvc.perform(get(URI_TEMPLATE).param(QUERY_STRING_KEY, SEARCH_TITLE))
              .andExpect(status().isUnauthorized());
    }
  }


  // -- 헬퍼 메서드 --
  private User persistKnowledge() {
    User user = User.builder()
            .nickname("박기윤")
            .kakaoId(KAKAO_ID)
            .build();
    em.persist(user);

    return user;
  }


  private void persistKnowledge(User user, String title) {
    Knowledge knowledge = Knowledge.builder()
            .user(user)
            .title(title)
            .content("지식 내용")
            .createdAt(FIXED_DATE)
            .build();
    em.persist(knowledge);
  }
}

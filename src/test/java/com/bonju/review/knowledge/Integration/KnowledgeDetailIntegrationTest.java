package com.bonju.review.knowledge.Integration;

import com.bonju.review.exception.ErrorResponse;
import com.bonju.review.knowledge.dto.KnowledgeDetailResponseDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.testsupport.security.WithMockKakaoUser;
import com.bonju.review.user.entity.User;
import com.bonju.review.util.enums.error_code.KnowledgeErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class KnowledgeDetailIntegrationTest {

  public static final String KAKAO_ID = "123";
  public static final LocalDateTime FIXED_DATE = LocalDateTime.of(2025, 5, 10, 0, 0);
  public static final String KNOWLEDGE_TITLE = "지식 제목";
  public static final String KNOWLEDGE_CONTENT = "지식 내용";
  public static final String PATH = "/knowledge/1";
  public static final String NON_EXISTING_KNOWLEDGE_PATH = "/knowledge/999";
  @Autowired
  EntityManager em;

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Test
  @DisplayName("로그인된 유저가 본인의 지식을 조회하면 200 OK와 함께 상세 정보를 반환한다.")
  @WithMockKakaoUser(kakaoId = KAKAO_ID)
  void shouldReturnKnowledgeDetail_WhenUserOwnsKnowledge() throws Exception {
    //given
    User user = registerUser("박기윤", KAKAO_ID);
    Knowledge savedKnowledge = registerKnowledge(user);

    clearEntityManager();
    //when
    String response = mockMvc.perform(get(PATH))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    KnowledgeDetailResponseDto actual = objectMapper.readValue(response, KnowledgeDetailResponseDto.class);

    //then
    assertThat(actual.getId()).isEqualTo(savedKnowledge.getId());
    assertThat(actual.getCreatedAt()).isEqualTo(FIXED_DATE);
    assertThat(actual.getTitle()).isEqualTo(KNOWLEDGE_TITLE);
    assertThat(actual.getContent()).isEqualTo(KNOWLEDGE_CONTENT);
  }

  @DisplayName("존재하지 않는 지식 ID로 요청하면 404를 반환한다")
  @Test
  @WithMockKakaoUser(kakaoId = KAKAO_ID)
  void shouldReturn404_WhenKnowledgeNotFound() throws Exception {
    // given
    registerUser("박기윤", KAKAO_ID);
    clearEntityManager();

    //when
    String response = mockMvc.perform(get(NON_EXISTING_KNOWLEDGE_PATH))
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse()
            .getContentAsString();

    ErrorResponse actual = objectMapper.readValue(response, ErrorResponse.class);

    //then
    assertThat(actual.getError()).isEqualTo(KnowledgeErrorCode.NOT_FOUND.getHttpStatus().getReasonPhrase());
    assertThat(actual.getStatus()).isEqualTo(KnowledgeErrorCode.NOT_FOUND.getHttpStatus().value());
    assertThat(actual.getMessage()).isEqualTo(KnowledgeErrorCode.NOT_FOUND.getMessage());
    assertThat(actual.getPath()).isEqualTo(NON_EXISTING_KNOWLEDGE_PATH);
  }

  @DisplayName("지식 ID가 0 이하일 경우 400을 반환한다")
  @ParameterizedTest(name = "[{index}] id={0}일 때 400 반환")
  @ValueSource(longs = {0, -1, -100})
  @WithMockKakaoUser(kakaoId = KAKAO_ID)
  void shouldReturn400_WhenIdIsInvalid(long invalidId) throws Exception {
    // when & then
    mockMvc.perform(get("/knowledge/" + invalidId))
            .andExpect(status().isBadRequest());
  }


  @DisplayName("로그인하지 않은 상태로 요청하면 401을 반환한다")
  @Test
  void shouldReturn401_WhenUnauthenticated() throws Exception {

    //when & then
    mockMvc.perform(get(PATH))
            .andExpect(status().isUnauthorized());
  }


  // -- 헬퍼 메서드 --
  private void clearEntityManager() {
    em.flush();
    em.clear();
  }

  private Knowledge registerKnowledge(User user) {
    Knowledge knowledge = createKnowledge(user);

    em.persist(knowledge);
    return knowledge;
  }

  private Knowledge createKnowledge(User user) {
    return Knowledge.builder()
            .user(user)
            .title(KNOWLEDGE_TITLE)
            .content(KNOWLEDGE_CONTENT)
            .createdAt(FIXED_DATE)
            .build();
  }

  private User registerUser(String nickname, String kakaoId) {
    User user = createUser(nickname, kakaoId);

    em.persist(user);
    return user;
  }

  private User createUser(String nickname, String kakaoId) {
    return User.builder()
            .nickname(nickname)
            .kakaoId(kakaoId)
            .build();
  }
}

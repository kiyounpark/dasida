package com.bonju.review.knowledge_quiz.integration;

import com.bonju.review.knowledge_quiz.dto.KnowledgeRegistrationRequestDto;
import com.bonju.review.quiz.client.AiClient;
import com.bonju.review.quiz.exception.errorcode.QuizErrorCode;
import com.bonju.review.testsupport.security.WithMockKakaoUser;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import com.bonju.review.util.RestApiTestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class KnowledgeRegistrationIntegrationTest {

  private static final String ENDPOINT = "/knowledge";

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;
  @Autowired EntityManager em;

  /* 외부 IO Mock */
  @MockitoBean AiClient aiClient;
  @MockitoBean UserService userService;

  /* ─────────────────────────────────────────────── */

  @Test
  @WithMockKakaoUser(kakaoId = "123")
  @DisplayName("정상 플로우: 201 + Knowledge/Quiz 실제 저장")
  @Transactional
  void success_persistsEntities() throws Exception {
    em.persist(dummyUser("123"));
    em.flush();
    em.clear();

    given(aiClient.generateRawQuizJson(any(), any()))
            .willReturn("""
                    [ {"question":"Q1","answer":"A1","hint":"H1"} ]
                """);

    String req = objectMapper.writeValueAsString(new KnowledgeRegistrationRequestDto("제목", "본문"));
    String location = mockMvc.perform(post(ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(req)
                    .with(csrf()))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getHeader("Location");

    Long id = extractId(location);

    assertThat(countKnowledgeById(id)).isEqualTo(1L);
    assertThat(countQuizByKnowledgeId(id)).isGreaterThan(0L);
  }

  @Test
  @WithMockKakaoUser(kakaoId = "123")
  @DisplayName("QuizException → 500, 전체 롤백")
  void quizException_rollbackAll() throws Exception {
    given(aiClient.generateRawQuizJson(any(), any())).willReturn("malformed json");

    String req = objectMapper.writeValueAsString(new KnowledgeRegistrationRequestDto("제목", "본문"));

    String body = mockMvc.perform(post(ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(req)
                    .with(csrf()))
            .andExpect(status().isInternalServerError())
            .andReturn()
            .getResponse()
            .getContentAsString();

    RestApiTestUtils.assertError(body, QuizErrorCode.QUIZ_MAPPING_FAILED, ENDPOINT);

    assertThat(countAllKnowledge()).isZero();
    assertThat(countAllQuiz()).isZero();
  }

  /* ────────────────── private helpers ────────────────── */

  private User dummyUser(String kakaoId) {
    return User.builder().kakaoId(kakaoId).nickname("테스트").build();
  }

  private Long extractId(String location) {
    return Long.parseLong(location.substring(location.lastIndexOf('/') + 1));
  }

  private long countKnowledgeById(Long id) {
    return em.createQuery(
                    "select count(k) from Knowledge k where k.id = :id", Long.class)
            .setParameter("id", id)
            .getSingleResult();
  }

  private long countQuizByKnowledgeId(Long id) {
    return em.createQuery(
                    "select count(q) from Quiz q where q.knowledge.id = :id", Long.class)
            .setParameter("id", id)
            .getSingleResult();
  }

  private long countAllKnowledge() {
    return em.createQuery("select count(k) from Knowledge k", Long.class)
            .getSingleResult();
  }

  private long countAllQuiz() {
    return em.createQuery("select count(q) from Quiz q", Long.class)
            .getSingleResult();
  }
}


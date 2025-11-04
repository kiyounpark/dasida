package com.bonju.review.knowledge_quiz.integration;

import com.bonju.review.knowledge.dto.KnowledgeRegisterRequestDto;
import com.bonju.review.knowledge_quiz.dto.KnowledgeQuizRegistrationResponseDto;
import com.bonju.review.quiz.client.AiClient;
import com.bonju.review.quiz.entity.Quiz;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
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
  @DisplayName("POST /knowledge – 퀴즈가 없을 때 201 Created와 needPushPermission=true 반환")
  @Transactional
  void registerKnowledge_withoutExistingQuiz_returns201AndPushPermissionTrue() throws Exception {
    em.persist(dummyUser("123"));
    em.flush();
    em.clear();

    given(aiClient.generateRawQuizJson(any(), any()))
            .willReturn("""
            [ {"question":"Q1","answer":"A1","hint":"H1"} ]
        """);

    String request = objectMapper.writeValueAsString(
            new KnowledgeRegisterRequestDto(
                    "제목",
                    "본문",
                    List.of("https://cdn.test/knowledge-image.png")
            )
    );

    String response = mockMvc.perform(post(ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request)
                    .with(csrf()))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    KnowledgeQuizRegistrationResponseDto actual =
            objectMapper.readValue(response, KnowledgeQuizRegistrationResponseDto.class);

    assertThat(actual.needPushPermission()).isTrue();
  }

  @Test
  @WithMockKakaoUser(kakaoId = "123")
  @DisplayName("퀴즈가 이미 있을 때 needPushPermission=false")
  @Transactional
  void success_persistsEntities_whenQuizExists() throws Exception {

    User user = dummyUser("123");
    em.persist(user);

    em.persist(Quiz.builder().user(user).build());   // 이미 존재하는 퀴즈
    em.flush();
    em.clear();

    given(userService.findUser()).willReturn(user);  // ← 반드시 스텁
    given(aiClient.generateRawQuizJson(any(), any()))
            .willReturn("""
        [ {"question":"Q1","answer":"A1","hint":"H1"} ]
        """);

    String req = objectMapper.writeValueAsString(
            new KnowledgeRegisterRequestDto(
                    "제목",
                    "본문",
                    List.of("https://cdn.test/knowledge-image.png")
            )
    );

    String res = mockMvc.perform(post(ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(req)
                    .with(csrf()))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    KnowledgeQuizRegistrationResponseDto dto =
            objectMapper.readValue(res, KnowledgeQuizRegistrationResponseDto.class);

    assertThat(dto.needPushPermission()).isFalse();  // ✅ 통과
  }

  @Test
  @WithMockKakaoUser(kakaoId = "123")
  @DisplayName("QuizException → 500, 전체 롤백")
  void quizException_rollbackAll() throws Exception {
    given(aiClient.generateRawQuizJson(any(), any())).willReturn("malformed json");

    String req = objectMapper.writeValueAsString(
            new KnowledgeRegisterRequestDto(
                    "제목",
                    "본문",
                    List.of("https://cdn.test/knowledge-image.png")
            )
    );

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

  private long countAllKnowledge() {
    return em.createQuery("select count(k) from Knowledge k", Long.class)
            .getSingleResult();
  }

  private long countAllQuiz() {
    return em.createQuery("select count(q) from Quiz q", Long.class)
            .getSingleResult();
  }
}


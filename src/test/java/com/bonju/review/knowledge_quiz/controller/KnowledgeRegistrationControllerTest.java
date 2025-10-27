package com.bonju.review.knowledge_quiz.controller;

import com.bonju.review.knowledge.exception.KnowledgeException;
import com.bonju.review.knowledge_quiz.dto.KnowledgeQuizRegistrationResponseDto;
import com.bonju.review.knowledge_quiz.workflow.KnowledgeQuizCreationWorkflow;
import com.bonju.review.knowledge_quiz.dto.KnowledgeRegistrationRequestDto;
import com.bonju.review.quiz.exception.errorcode.QuizErrorCode;
import com.bonju.review.quiz.exception.exception.QuizException;
import com.bonju.review.slack.SlackErrorMessageFactory;
import com.bonju.review.util.RestApiTestUtils;
import com.bonju.review.util.enums.error_code.KnowledgeErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KnowledgeRegistrationController.class)
class KnowledgeRegistrationControllerTest {

  private static final String ENDPOINT = "/knowledge";
  private static final String TITLE    = "테스트 제목";
  private static final String CONTENT  = "###테스트 내용";

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  @MockitoBean KnowledgeQuizCreationWorkflow workflow;

  @MockitoBean SlackErrorMessageFactory slackErrorMessageFactory;

  @Test
  @DisplayName("POST /knowledge – 지식 등록 시 201 Created와 푸시 권한 요청 여부 플래그를 반환한다")
  @WithMockUser
  void registerKnowledge_returnsCreatedAndPushPermissionFlag() throws Exception {
    // given
    KnowledgeQuizRegistrationResponseDto knowledgeQuizRegistrationResponseDto =
            new KnowledgeQuizRegistrationResponseDto(true);

    given(workflow.registerKnowledgeAndGenerateQuizList(anyString(), anyString()))
            .willReturn(knowledgeQuizRegistrationResponseDto);

    String body = objectMapper.writeValueAsString(new KnowledgeRegistrationRequestDto(TITLE, CONTENT));

    // when
    String response = mockMvc.perform(post(ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
                    .with(csrf()))
            // then
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    KnowledgeQuizRegistrationResponseDto actual = objectMapper.readValue(response, KnowledgeQuizRegistrationResponseDto.class);
    assertThat(actual.needPushPermission()).isTrue();
  }

  @Nested
  @DisplayName("검증 실패")
  class ValidationFailure {

    @Test
    @DisplayName("타이틀이 공백이면 400 Bad Request")
    @WithMockUser
    void returnsBadRequest_whenTitleBlank() throws Exception {
      String body = objectMapper.writeValueAsString(new KnowledgeRegistrationRequestDto(" ", CONTENT));

      mockMvc.perform(post(ENDPOINT)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(body)
                      .with(csrf()))
              .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("콘텐츠가 공백이면 400 Bad Request")
    @WithMockUser
    void returnsBadRequest_whenContentBlank() throws Exception {
      String body = objectMapper.writeValueAsString(new KnowledgeRegistrationRequestDto(TITLE, " "));

      mockMvc.perform(post(ENDPOINT)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(body)
                      .with(csrf()))
              .andExpect(status().isBadRequest());

    }



  @Nested
  @DisplayName("내부 예외 → 500 & 슬랙 호출")
  class InternalFailure {

    @Test
    @DisplayName("KnowledgeException(INTERNAL_SERVER_ERROR) 이면 500과 슬랙 호출 1회")
    @WithMockUser
    void returns500_whenKnowledgeException_andCallsSlack() throws Exception {
      // given ─ 워크플로가 KnowledgeException 을 던지도록 스텁
      KnowledgeException ex = new KnowledgeException(
              KnowledgeErrorCode.REGISTER_FAILED,
              new RuntimeException("DB down")
      );
      given(workflow.registerKnowledgeAndGenerateQuizList(anyString(), anyString()))
              .willThrow(ex);

      given(slackErrorMessageFactory.createErrorMessage(any(), any()))
              .willReturn("dummy slack message");

      String body = objectMapper.writeValueAsString(
              new KnowledgeRegistrationRequestDto(TITLE, CONTENT));

      // when
      String response = mockMvc.perform(post(ENDPOINT)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(body)
                      .with(csrf()))
              // then
              .andExpect(status().isInternalServerError())
              .andReturn()
              .getResponse()
              .getContentAsString();

      // SlackErrorMessageFactory 가 정확히 한 번 호출됐는지 검증
      verify(slackErrorMessageFactory, times(1))
              .createErrorMessage(any(), eq(ex));

      RestApiTestUtils.assertError(response, KnowledgeErrorCode.REGISTER_FAILED, ENDPOINT);
    }

    @Test
    @DisplayName("QuizException(QUIZ_SAVE_FAILED) 이면 500과 슬랙 호출 1회")
    @WithMockUser
    void returns500_whenQuizException_andCallsSlack() throws Exception {
      // given ─ 워크플로가 QuizException 을 던지게 스텁
      QuizException ex = new QuizException(
              QuizErrorCode.QUIZ_SAVE_FAILED,
              new RuntimeException("insert fail")
      );
      given(workflow.registerKnowledgeAndGenerateQuizList(anyString(), anyString()))
              .willThrow(ex);

      given(slackErrorMessageFactory.createErrorMessage(any(), any()))
              .willReturn("dummy slack message");

      String body = objectMapper.writeValueAsString(
              new KnowledgeRegistrationRequestDto(TITLE, CONTENT));

      // when
      String response = mockMvc.perform(post(ENDPOINT)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(body)
                      .with(csrf()))
              // then
              .andExpect(status().isInternalServerError())
              .andReturn()
              .getResponse()
              .getContentAsString();

      verify(slackErrorMessageFactory, times(1))
              .createErrorMessage(any(), eq(ex));

      RestApiTestUtils.assertError(response, QuizErrorCode.QUIZ_SAVE_FAILED, ENDPOINT);
    }
  }
}

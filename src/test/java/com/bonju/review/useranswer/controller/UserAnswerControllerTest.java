package com.bonju.review.useranswer.controller;

import com.bonju.review.useranswer.command.SubmitUserAnswerCommand;
import com.bonju.review.useranswer.dto.UserAnswerRequestDto;
import com.bonju.review.useranswer.dto.UserAnswerResponseDto;
import com.bonju.review.useranswer.service.UserAnswerService;
import com.bonju.review.slack.SlackErrorMessageFactory;
import com.bonju.review.useranswer.exception.UserAnswerErrorCode;
import com.bonju.review.useranswer.exception.UserAnswerException;
import com.bonju.review.util.RestApiTestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserAnswerController.class)
class UserAnswerControllerTest {

  public static final int DAY_TYPE = 0;
  public static final String USER_ANSWER_DUMMY_DATA = "userAnswer";
  private static final String ENDPOINT = "/quizzes/1/answers";

  @MockitoBean
  SlackErrorMessageFactory slackErrorMessageFactory;

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockitoBean
  UserAnswerService userAnswerService;


  @Nested
  @DisplayName("정상 제출 → 200 OK")
  class SubmitSuccess {

    @ParameterizedTest(name = "[{index}] isCorrect → {0}")
    @ValueSource(booleans = {true, false})
    @WithMockUser
    void submit_returns_correct_flag(boolean expectedCorrect) throws Exception {
      // given
      UserAnswerResponseDto mocked = new UserAnswerResponseDto(expectedCorrect);
      String body = objectMapper.writeValueAsString(
              new UserAnswerRequestDto(USER_ANSWER_DUMMY_DATA, DAY_TYPE));

      given(userAnswerService.submitAnswer(any(SubmitUserAnswerCommand.class)))
              .willReturn(mocked);

      // when
      String rsp = mockMvc.perform(post(ENDPOINT)
                      .with(csrf())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(body))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse()
              .getContentAsString();

      // then
      UserAnswerResponseDto actual = objectMapper.readValue(rsp, UserAnswerResponseDto.class);
      assertThat(actual.isCorrect()).isEqualTo(expectedCorrect);
    }
  }


  @Nested
  @DisplayName("예외 상황 → 500 & 슬랙 호출")
  class ErrorHandling {

    @Test
    @DisplayName("DB 저장 실패 시 500 & 슬랙")
    @WithMockUser
    void returns500_whenDbSaveFails_andCallsSlack() throws Exception {
      // given
      UserAnswerException ex = new UserAnswerException(
              UserAnswerErrorCode.DB_SAVE_FAIL,
              new RuntimeException("DB down"));

      given(userAnswerService.submitAnswer(any(SubmitUserAnswerCommand.class)))
              .willThrow(ex);
      given(slackErrorMessageFactory.createErrorMessage(any(), any()))
              .willReturn("dummy slack message");

      String body = objectMapper.writeValueAsString(
              new UserAnswerRequestDto(USER_ANSWER_DUMMY_DATA, DAY_TYPE));

      // when
      String rsp = mockMvc.perform(post(ENDPOINT)
                      .with(csrf())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(body))
              .andExpect(status().isInternalServerError())
              .andReturn()
              .getResponse()
              .getContentAsString();

      // then
      verify(slackErrorMessageFactory, times(1))
              .createErrorMessage(any(), eq(ex));

      RestApiTestUtils.assertError(rsp, UserAnswerErrorCode.DB_SAVE_FAIL, ENDPOINT);
    }
  }

  @Nested
  @DisplayName("입력 검증 실패 → 400 Bad Request")
  class ValidationFailure {

    /**  ❚ 실패 케이스 JSON 목록 */
    static Stream<String> invalidBodies() {
      return Stream.of(
              // answer 가 비어 있음
              "{\"answer\":\"\",\"day_type\":0}",
              // answer 가 공백만 있음
              "{\"answer\":\"   \",\"day_type\":0}",
              // answer 필드 누락
              "{\"day_type\":0}",
              // day_type 필드 누락
              "{\"answer\":\"정답\"}"
      );
    }

    @ParameterizedTest(name = "[{index}] 잘못된 본문 ⇒ 400")
    @MethodSource("invalidBodies")
    @WithMockUser
    void badRequest_whenInvalidInput(String body) throws Exception {
      // when - then
      mockMvc.perform(post(ENDPOINT)
                      .with(csrf())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(body))
              .andExpect(status().isBadRequest());

      // DTO 검증 단계에서 바로 실패하므로, 아래 두 컴포넌트는 호출되지 않아야 한다
      verifyNoInteractions(userAnswerService);
    }
  }
}
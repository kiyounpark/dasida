package com.bonju.review.devicetoken.controller;

import com.bonju.review.devicetoken.dto.DeviceTokenRequestDto;
import com.bonju.review.devicetoken.exception.DeviceTokenErrorCode;
import com.bonju.review.devicetoken.exception.DeviceTokenException;
import com.bonju.review.devicetoken.service.DeviceTokenService;
import com.bonju.review.slack.SlackErrorMessageFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeviceTokenController.class)
class DeviceTokenControllerTest {

  public static final String END_POINT = "/token";
  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockitoBean
  DeviceTokenService deviceTokenService;

  @MockitoBean
  SlackErrorMessageFactory slackErrorMessageFactory;

  @Test
  @DisplayName("POST /device-token – 200 OK 가 반환된다")
  @WithMockUser
  void postDeviceToken_returns200() throws Exception {
    // given
    String fixedToken = "sample-token";
    given(deviceTokenService.getOrCreateToken(fixedToken))
            .willReturn(fixedToken);

    DeviceTokenRequestDto request = new DeviceTokenRequestDto(fixedToken);

    // when & then
    mockMvc.perform(
            post(END_POINT)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
    ).andExpect(status().isOk());
  }

  @Test
  @DisplayName("POST /token – DB 오류 시 500, 예외 내부에 DataAccessException 이 포함된다")
  @WithMockUser
  void postDeviceToken_dbFail_wrapsDataAccessException() throws Exception {
    // given
    String token = "sample-token";
    DataAccessException dataAccessEx = new DataAccessException("DB down"){};
    DeviceTokenException wrapped =
            new DeviceTokenException(DeviceTokenErrorCode.DB_FAIL, dataAccessEx);

    given(deviceTokenService.getOrCreateToken(token)).willThrow(wrapped);
    given(slackErrorMessageFactory.createErrorMessage(any(), any()))
            .willReturn("dummy");

    DeviceTokenRequestDto request = new DeviceTokenRequestDto(token);

    // when & then: HTTP 500
    mockMvc.perform(post(END_POINT)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isInternalServerError());

    // ── 캡처로 예외 내부 원인(DataAccessException) 확인 ──
    ArgumentCaptor<DeviceTokenException> captor =
            ArgumentCaptor.forClass(DeviceTokenException.class);
    verify(slackErrorMessageFactory).createErrorMessage(any(), captor.capture());

    DeviceTokenException captured = captor.getValue();
    assertThat(captured.getCause())
            .isInstanceOf(DataAccessException.class);
  }
}
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeviceTokenController.class)
class DeviceTokenControllerTest {

  private static final String END_POINT = "/token";
  private static final String TOKEN     = "sample-token";

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  @MockitoBean DeviceTokenService       deviceTokenService;
  @MockitoBean SlackErrorMessageFactory slackErrorMessageFactory;

  /* ─────────── 200 OK 검증 ─────────── */
  @Test
  @DisplayName("POST /token – 200 OK(본문 없음) 반환")
  @WithMockUser
  void register_returns200() throws Exception {
    willDoNothing().given(deviceTokenService).registerDeviceToken(TOKEN);

    DeviceTokenRequestDto request = new DeviceTokenRequestDto(TOKEN);

    mockMvc.perform(post(END_POINT)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
  }

  /* ─────────── 400 Bad Request 검증 ─────────── */
  @Test
  @DisplayName("POST /token – 요청 바디가 유효하지 않으면 400 Bad Request")
  @WithMockUser
  void register_invalidRequest_returns400() throws Exception {
    // 토큰이 빈 문자열이면 @NotBlank 검증 실패
    DeviceTokenRequestDto badRequest = new DeviceTokenRequestDto("");

    mockMvc.perform(post(END_POINT)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest());

    // 서비스가 호출되지 않아야 함
    then(deviceTokenService).should(never()).registerDeviceToken(any());
  }

  /* ─────────── 500 & 래핑 예외 검증 ─────────── */
  @Test
  @DisplayName("POST /token – DB 오류 시 500, 원인 예외가 DataAccessException")
  @WithMockUser
  void register_dbFail_returns500_andWrapsCause() throws Exception {
    DataAccessException dataAccessEx = new DataAccessException("DB down"){};
    DeviceTokenException wrapped =
            new DeviceTokenException(DeviceTokenErrorCode.DB_FAIL, dataAccessEx);

    willThrow(wrapped).given(deviceTokenService).registerDeviceToken(TOKEN);
    given(slackErrorMessageFactory.createErrorMessage(any(), any()))
            .willReturn("dummy");

    DeviceTokenRequestDto request = new DeviceTokenRequestDto(TOKEN);

    mockMvc.perform(post(END_POINT)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isInternalServerError());

    ArgumentCaptor<DeviceTokenException> captor =
            ArgumentCaptor.forClass(DeviceTokenException.class);
    verify(slackErrorMessageFactory).createErrorMessage(any(), captor.capture());

    assertThat(captor.getValue().getCause())
            .isInstanceOf(DataAccessException.class);
  }
}
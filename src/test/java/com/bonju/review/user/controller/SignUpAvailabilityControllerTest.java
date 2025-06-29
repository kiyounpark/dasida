package com.bonju.review.user.controller;

import com.bonju.review.exception.ErrorResponse;
import com.bonju.review.slack.SlackErrorMessageFactory;
import com.bonju.review.user.dto.SignUpAvailabilityResponseDto;
import com.bonju.review.user.exception.UserErrorCode;
import com.bonju.review.user.exception.UserException;
import com.bonju.review.user.service.SignUpLimitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SignUpAvailabilityController.class)
@AutoConfigureMockMvc(addFilters = false) // security 관련 쓸데없는 코드가 많아 통합테스트에서 제대로 확인
class SignUpAvailabilityControllerTest {

  @Autowired MockMvc        mockMvc;
  @Autowired ObjectMapper   objectMapper;   // 스프링 부트가 자동 빈 등록

  @MockitoBean SignUpLimitService signUpLimitService;

  @MockitoBean
  SlackErrorMessageFactory slackErrorMessageFactory;


  @Test
  @DisplayName("GET /login-enabled → 200 OK + 응답 본문을 DTO 로 매핑해 검증")
  void get_loginEnabled_maps_body_to_dto() throws Exception {
    // given : 서비스가 true 반환하도록 세팅
    SignUpAvailabilityResponseDto dto = new SignUpAvailabilityResponseDto(true);
    given(signUpLimitService.checkSignUpAvailability()).willReturn(dto);

    // when : 요청 수행 후 결과 객체 확보
    MvcResult mvcResult = mockMvc.perform(get("/login-enabled"))
            .andExpect(status().isOk())
            .andReturn();

    // then : 본문(JSON) → DTO 역직렬화해서 필드 검증
    String jsonBody = mvcResult.getResponse().getContentAsString();
    SignUpAvailabilityResponseDto response =
            objectMapper.readValue(jsonBody, SignUpAvailabilityResponseDto.class);

    assertThat(response.canSignUp()).isTrue();         // 필드 값 확인
    verify(signUpLimitService, times(1)).checkSignUpAvailability(); // 호출 1회 검증
  }

  @Test
  @DisplayName("GET /login-enabled → UserException 발생 시 500 + ErrorResponse 반환")
  void get_loginEnabled_when_service_throws_returns_error_response() throws Exception {
    // given : 서비스가 예외를 던지도록 세팅
    given(signUpLimitService.checkSignUpAvailability())
            .willThrow(new UserException(UserErrorCode.COUNT_FAIL));

    // 슬랙 메시지 팩토리 호출까지 막아둠
    given(slackErrorMessageFactory.createErrorMessage(any(), any()))
            .willReturn("[skip]");

    // when
    MvcResult mvcResult = mockMvc.perform(get("/login-enabled"))
            .andExpect(status().isInternalServerError())
            .andReturn();

    // then : ErrorResponse 매핑 후 필드 검증
    ErrorResponse error =
            objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                    ErrorResponse.class);

    assertThat(error.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    assertThat(error.getMessage()).isEqualTo(UserErrorCode.COUNT_FAIL.getMessage());

    verify(signUpLimitService, times(1)).checkSignUpAvailability();
    verify(slackErrorMessageFactory, times(1))
            .createErrorMessage(any(), any(UserException.class));
  }
}
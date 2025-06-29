package com.bonju.review.user.integration;

import com.bonju.review.user.dto.SignUpAvailabilityResponseDto;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static com.bonju.review.user.service.SignUpLimitService.MAX_USER_COUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Sign-up 제한 로직 통합 테스트 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional                               // 각 테스트 후 롤백
class SignUpAvailabilityIntegrationTest {

  @Autowired MockMvc       mockMvc;
  @Autowired ObjectMapper  objectMapper;
  @Autowired UserRepository userRepository;


  // ------------------------------------------------------------------
  @Test
  @DisplayName("사용자 수가 한도 미만이면 canSignUp = true")
  void canSignUp_when_userCount_is_below_limit() throws Exception {
    // given
    insertUsers(500);   // 1000 미만

    // when
    SignUpAvailabilityResponseDto body = callApi();

    // then
    assertThat(body.canSignUp()).isTrue();
  }

  // ------------------------------------------------------------------
  @Test
  @DisplayName("사용자 수가 한도(1000) 이상이면 canSignUp = false")
  void cannotSignUp_when_userCount_reaches_limit() throws Exception {
    // given
    insertUsers(MAX_USER_COUNT);   // 1000

    // when
    SignUpAvailabilityResponseDto body = callApi();

    // then
    assertThat(body.canSignUp()).isFalse();
  }

  // ====== 헬퍼 ======
  private void insertUsers(int count) {
    for (int i = 0; i < count; i++) {
      userRepository.save(User.builder()
              .kakaoId("kakao-" + i)
              .nickname("user" + i)
              .build());
    }
  }

  private SignUpAvailabilityResponseDto callApi() throws Exception {
    MvcResult mvcResult = mockMvc.perform(get("/login-enabled"))
            .andExpect(status().isOk())
            .andReturn();
    return objectMapper.readValue(
            mvcResult.getResponse().getContentAsString(),
            SignUpAvailabilityResponseDto.class);
  }
}

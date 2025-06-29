package com.bonju.review.user.service;

import com.bonju.review.user.dto.SignUpAvailabilityResponse;
import com.bonju.review.user.exception.UserErrorCode;
import com.bonju.review.user.exception.UserException;
import com.bonju.review.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.TransientDataAccessResourceException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SignUpLimitServiceTest {

  private static final long MAX_USER_COUNT = 1_000L;   // 서비스 클래스의 상수와 동일해야 함

  @Mock
  UserRepository userRepository;

  // 생성자 주입 필드를 가진 실제 Service 구현체 주입
  @InjectMocks
  SignUpLimitService signUpLimitService;

  /* ─────────────────────────────────────────────
   * 정상 흐름
   * ─────────────────────────────────────────── */
  @Nested
  @DisplayName("정상 케이스")
  class Success {

    @Test
    @DisplayName("현재 유저 수 < 한도  →  available = true")
    void returns_available_true_when_below_limit() {
      // given
      given(userRepository.count()).willReturn(MAX_USER_COUNT - 1);

      // when
      SignUpAvailabilityResponse res = signUpLimitService.checkSignUpAvailability();

      // then
      assertThat(res.canSignUp()).isTrue();
      verify(userRepository).count();
    }

    @Test
    @DisplayName("현재 유저 수 ≥ 한도  →  available = false")
    void returns_available_false_when_over_limit() {
      // given
      given(userRepository.count()).willReturn(MAX_USER_COUNT);

      // when
      SignUpAvailabilityResponse res = signUpLimitService.checkSignUpAvailability();

      // then
      assertThat(res.canSignUp()).isFalse();
      verify(userRepository).count();
    }
  }

  /* ─────────────────────────────────────────────
   * 예외 흐름
   * ─────────────────────────────────────────── */
  @Nested
  @DisplayName("예외 케이스")
  class Failure {

    @Test
    @DisplayName("DataAccessException 발생 시 UserException 으로 감싸 던진다")
    void wraps_data_access_exception() {
      // given – DB 커넥션 풀 고갈 시 나올 법한 예외를 스텁
      DataAccessException dae =
              new TransientDataAccessResourceException("Connection exhausted");
      given(userRepository.count()).willThrow(dae);

      // when & then
      assertThatThrownBy(() -> signUpLimitService.checkSignUpAvailability())
              .isInstanceOf(UserException.class)
              .extracting("errorCode")             // UserException#getErrorCode
              .isEqualTo(UserErrorCode.COUNT_FAIL);

      // 원인(exception cause)이 그대로 보존됐는지 확인
      assertThatThrownBy(() -> signUpLimitService.checkSignUpAvailability())
              .hasRootCause(dae);
    }
  }
}
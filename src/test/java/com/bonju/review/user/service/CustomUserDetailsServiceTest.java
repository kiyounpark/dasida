package com.bonju.review.user.service;

import com.bonju.review.user.entity.User;
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
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

/**
 * 단위 테스트 : CustomUserDetailsService
 *
 * - 하나의 개념만 검증 (성공 vs 실패 각각 독립)
 * - 외부 의존성(UserRepository) 목킹
 * - 고정 입력값 사용
 */
@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

  private static final String KAKAO_ID = "1234567890";

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private CustomUserDetailsService service;

  @Nested
  @DisplayName("loadUserByUsername 성공 시")
  class Success {

    @Test
    @DisplayName("UserDetails 를 반환한다")
    void returnsUserDetails() {
      // given
      User stubUser = User.builder()
              .kakaoId(KAKAO_ID)
              .nickname("tester")
              .build();
      given(userRepository.findByKaKaoId(KAKAO_ID))
              .willReturn(Optional.of(stubUser));

      // when
      UserDetails result = service.loadUserByUsername(KAKAO_ID);

      // then
      assertThat(result.getUsername()).isEqualTo(KAKAO_ID);
      assertThat(result.getPassword()).isEmpty();
      assertThat(result.getAuthorities())
              .extracting("authority")
              .containsExactly("ROLE_USER");

      then(userRepository).should().findByKaKaoId(KAKAO_ID);
    }
  }

  @Nested
  @DisplayName("loadUserByUsername 실패 시")
  class Failure {

    @Test
    @DisplayName("USER_NOT_FOUND 예외를 던진다")
    void throwsUserExceptionWhenNotFound() {
      // given
      given(userRepository.findByKaKaoId(KAKAO_ID))
              .willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> service.loadUserByUsername(KAKAO_ID))
              .isInstanceOf(UserException.class)
              .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.USER_NOT_FOUND);

      then(userRepository).should().findByKaKaoId(KAKAO_ID);
    }
  }
}